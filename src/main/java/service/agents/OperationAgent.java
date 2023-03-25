package service.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import service.util.MyLog;
import service.annotationsetup.SetAnnotationNumber;
import service.behaviour.MyBehaviour;
import service.models.cooker.Cooker;
import service.models.operation.OperationLog;
import service.util.JSONParser;
import service.util.Theme;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Agent operation.
 * Possible actions:
 * 1. Requests the Management Agent to reserve the cook and equipment to perform the operation
 */
//@JadeAgent(number = 5)
public class OperationAgent extends Agent implements SetAnnotationNumber {
    private static String AGENT_TYPE = AgentTypes.OPERATION_AGENT;
    public static AtomicInteger operationIdCounter = new AtomicInteger(-1);

    private Cooker cooker;
    private Double timeout;
    private Integer processID;
    private Integer dishCardID;
    private Date begin;
    private Date end;
    private Integer cookerID;

    private Integer myID;
    private String cookerAgentName;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Cooker) {
                cooker = (Cooker) args[0];
            }
            if (args[1] instanceof Double) {
                timeout = (Double) args[1];
            }
            if (args[2] instanceof Integer) {
                processID = (Integer) args[2];
            }
            if (args[3] instanceof Integer) {
                dishCardID = (Integer) args[3];
            }
            if (args[4] instanceof Integer) {
                cookerID = (Integer) args[4];
            }
            if (args[5] instanceof Integer) {
                myID = (Integer) args[5];
            }
            if (args[6] instanceof String) {
                cookerAgentName = (String) args[6];
            }
        }

        Theme.print(AGENT_TYPE + ": " + getName() + " with myID=" + myID + " is ready." + cookerID + " " + cooker, Theme.GREEN);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentTypes.EQUIPMENT_AGENT);
        sd.setName(AgentTypes.EQUIPMENT_AGENT);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        begin = new Date();
        doWait((int) (timeout * 10000));
        end = new Date();

        addBehaviour(new ListenServer());
        addBehaviour(new MyBehaviour(JSONParser.gson.toJson("Success"), OntologiesTypes.OPERATION_COOKER, AgentTypes.COOKER_AGENT, cookerAgentName));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        Theme.print(getAID().getName() + " terminating", Theme.RED);
    }

    @Override
    public void setNumber(int number) {
        SetAnnotationNumber.super.setNumber(number);
    }

    private class ListenServer extends Behaviour {

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getOntology().equals(OntologiesTypes.COOKER_OPERATION)) {
                    var dishCardJson = msg.getContent();
                    Theme.print(AGENT_TYPE + " " + myAgent.getName() + " got message from " + msg.getSender().getName() + ": " + dishCardJson, Theme.RESET);
                    OperationLog operationLog = new OperationLog(myID, processID, dishCardID, begin, end, cooker.cook_id, false);

                    MyLog.LogOperation(JSONParser.gson.toJson(operationLog));

                    myAgent.doDelete();
                }

            } else {
                block();
            }
        }

        @Override
        public boolean done() {
            return false;
        }
    }
}
