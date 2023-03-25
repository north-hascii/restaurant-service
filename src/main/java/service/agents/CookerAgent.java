package service.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import service.annotationsetup.SetAnnotationNumber;
import service.behaviour.MyBehaviour;
import service.models.OperationIdList;
import service.models.cooker.Cooker;
import service.models.dishCard.DishCard;
import service.models.operation.Operation;
import service.util.info.JSONParser;
import service.util.env.Theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Agent cook. Represents a specific person
 * Possible actions:
 * 1. The agent receives a task from the Process agent and executes it,
 *  creating the process and killing it upon completion
 */
//@JadeAgent(number = 5)
public class CookerAgent extends Agent implements SetAnnotationNumber {
    private static String AGENT_TYPE = AgentTypes.COOKER_AGENT;

    private Cooker cooker;
    private Integer cookerID;
    private AtomicInteger CookerOperationCounter = new AtomicInteger(0);

    private ArrayList<Integer> opersID = new ArrayList<>();

    String processAgentName;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Cooker) {
                cooker = (Cooker) args[0];
            }
            if (args[1] instanceof Integer) {
                cookerID = (Integer) args[1];
            }
        }
        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);
        Theme.print(AGENT_TYPE + ": " + cooker, Theme.PURPLE);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentTypes.COOKER_AGENT);
        sd.setName(AgentTypes.COOKER_AGENT);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new ListenServer(this));
    }

    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Print out a dismissal message
        Theme.print("testAgent " + getAID().getName() + " terminating", Theme.RED);
    }

    @Override
    public void setNumber(int number) {
        SetAnnotationNumber.super.setNumber(number);
    }

    private class ListenServer extends Behaviour {
        CookerAgent cookerAgent;
        private Integer processID;

        public ListenServer(CookerAgent cookerAgent) {
            this.cookerAgent = cookerAgent;
        }

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getOntology().equals(OntologiesTypes.PROCESS_COOKER)) {
                    var dataFromProcessJSON = msg.getContent();

                    Theme.print(AGENT_TYPE + " " + myAgent.getName() +
                            " got message from " + msg.getSender().getName() + ": " + dataFromProcessJSON, Theme.RESET
                    );
                    Theme.print(AGENT_TYPE + " " + myAgent.getName() + " " + cooker, Theme.RESET);

                    var dataFromProcess = JSONParser.gson.fromJson(dataFromProcessJSON, DataFromProcess.class);
                    var dishCard = dataFromProcess.dishCard;
                    processID = dataFromProcess.processID;
                    processAgentName = dataFromProcess.processAgentName;


                    cookDish(dishCard);
                } else if (msg.getOntology().equals(OntologiesTypes.OPERATION_COOKER)) {
//                    Theme.print(myAgent.getName() + " before deleting operCounter=" + CookerOperationCounter, Theme.PURPLE);

                    addBehaviour(new MyBehaviour(JSONParser.gson.toJson("Delete OperationAgent"),
                            OntologiesTypes.COOKER_OPERATION,
                            msg.getSender())
                    );

                    var operationCounter = CookerOperationCounter.decrementAndGet();
                    if (operationCounter == 0 && startedOperations.get()) {
                        addBehaviour(new MyBehaviour(JSONParser.gson.toJson(new OperationIdList(opersID)),
                                OntologiesTypes.COOKER_PROCESS,
                                AgentTypes.PROCESS_AGENT,
                                processAgentName)
                        );
                    }
                }
            } else {
                block();
            }
        }

        private static AtomicBoolean startedOperations = new AtomicBoolean(false);

        HashMap<Integer, List<Operation>> operationLevelMap = new HashMap<>();

        private void cookDish(DishCard dishCard) {
            opersID = new ArrayList<>();
            operationLevelMap = new HashMap<>();
            for (var operation : dishCard.operations) {
                var list = operationLevelMap.get(operation.oper_async_point);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(operation);
                operationLevelMap.put(operation.oper_async_point, list);
            }
            HashSet<Integer> concurrentLevelSet = new HashSet<>(operationLevelMap.keySet());
            var controller = myAgent.getContainerController();
            for (var concurrentLevel : concurrentLevelSet) {
                var operations = operationLevelMap.get(concurrentLevel);
                for (var op : operations) {
                    try {
                        var operationID = OperationAgent.operationIdCounter.incrementAndGet();
                        CookerOperationCounter.incrementAndGet();
                        opersID.add(operationID);
                        Theme.print(myAgent.getName() +
                                " started operation: " +
                                operationID +
                                " currOperCounter=" +
                                CookerOperationCounter, Theme.PURPLE
                        );
                        controller.createNewAgent("OperationAgent" +
                                        operationID,
                                OperationAgent.class.getName(),
                                new Object[]{
                                        cooker,
                                        op.oper_time,
                                        processID,
                                        dishCard.card_id,
                                        cookerID,
                                        operationID,
                                        myAgent.getName()}
                        ).start();
                        startedOperations.set(true);


                    } catch (StaleProxyException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public boolean done() {
            return false;
        }
    }
}
