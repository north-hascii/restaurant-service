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
import service.models.cooker.Cooker;
import service.models.dishCard.DishCard;
import service.models.operation.Operation;
import service.util.JSONParser;
import service.util.Theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Agent cook. Represents a specific person
 * Possible actions:
 * 1. Receives a task and spends the specified time on it
 */
//@JadeAgent(number = 5)
public class CookerAgent extends Agent implements SetAnnotationNumber {
    private static String AGENT_TYPE = AgentTypes.COOKER_AGENT;
    private Integer processCounter = 0;

    private Cooker cooker;
    private Integer cookerID;
    private AtomicInteger CookerOperationCounter = new AtomicInteger(0);
    private List<Operation> operationList = new ArrayList<>();

    private ArrayList<Integer> opersID = new ArrayList<>();

//    private int process
//    private String processAgent = "1";


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
//        System.out.println(cooker + " " + cookerID);

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

                    Theme.print(AGENT_TYPE + " " + myAgent.getName() + " got message from " + msg.getSender().getName() + ": " + dataFromProcessJSON, Theme.RESET);

                    var dataFromProcess = JSONParser.gson.fromJson(dataFromProcessJSON, DataFromProcess.class);
                    var dishCard = dataFromProcess.dishCard;
                    processID = dataFromProcess.processID;

                    cookerAgent.cooker.cook_active = false;
                    cookDish(dishCard);
                } else if (msg.getOntology().equals(OntologiesTypes.OPERATION_COOKER)) {
                    addBehaviour(new MyBehaviour(JSONParser.gson.toJson("Delete OperationAgent"), OntologiesTypes.COOKER_OPERATION, msg.getSender()));
                    var operationCounter = CookerOperationCounter.decrementAndGet();
//                    System.out.println("After deleting operation: " + operCounter);
                    if (operationCounter == 0 && startedOperations.get()) {
//                        System.out.println("from ProcessAgent: " + processID);
                        addBehaviour(new MyBehaviour(JSONParser.gson.toJson(new OperationIdList(opersID)),
                                OntologiesTypes.COOKER_PROCESS, AgentTypes.PROCESS_AGENT, processID - 1));
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
                        CookerOperationCounter.addAndGet(1);
                        opersID.add(operationID);
                        controller.createNewAgent("OperationAgent" + operationID,
                                OperationAgent.class.getName(),
                                new Object[]{cooker, op.oper_time, processID, dishCard.card_id, cookerID, operationID}).start();
                        startedOperations.set(true);

                        System.out.println(myAgent.getName() + ": " + operationID);
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
