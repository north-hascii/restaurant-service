package service.agents;

import jade.core.AID;
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

import static service.util.DataBase.operationCounter;

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
        private String processAgent = "22";

//        private AID process;

        public ListenServer(CookerAgent cookerAgent) {
            this.cookerAgent = cookerAgent;
        }

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getOntology().equals(OntologiesTypes.PROCESS_COOKER)) {
//                    processAgent = msg.getSender();
                    var dataFromProcessJSON = msg.getContent();

                    Theme.print(AGENT_TYPE + " " + myAgent.getName() + " got message from " +
                            msg.getSender().getName() + ": " + dataFromProcessJSON, Theme.RESET
                    );

                    var dataFromProcess = JSONParser.gson.fromJson(dataFromProcessJSON, DataFromProcess.class);
                    var dishCard = dataFromProcess.dishCard;
                    processID = dataFromProcess.processID;
//                    cookerAgent.cooker.cook_active = true;
//                    myAgent.doWait(10000);
                    cookerAgent.cooker.cook_active = false;
//                    System.out.println(dishCard);
//                    System.out.println("!!!!!!!!! " +  processID);
//                    processCounter++;
                    cookDish(dishCard);
                } else if (msg.getOntology().equals(OntologiesTypes.OPERATION_COOKER)) {
                    addBehaviour(new MyBehaviour(JSONParser.gson.toJson("Delete OperationAgent"), OntologiesTypes.COOKER_OPERATION, msg.getSender()));
//                    Cooker = new AtomicInteger(CookerOperationCounter.get() - 1);
                    CookerOperationCounter.decrementAndGet();
                    System.out.println("After deleting operation: " + CookerOperationCounter);
                    if (CookerOperationCounter.get() == 0 && startedOperations.get()) {
                        System.out.println("from ProcessAgent: " + processID);
                        addBehaviour(new MyBehaviour(JSONParser.gson.toJson(operationList), OntologiesTypes.COOKER_PROCESS, AgentTypes.PROCESS_AGENT, processID - 1));
                    }
                }
            } else {
                block();
            }
        }

        //        private static AtomicInteger operationCounter = new AtomicInteger(0);
        private static AtomicBoolean startedOperations = new AtomicBoolean(false);

        HashMap<Integer, List<Operation>> operationLevelMap = new HashMap<>();

        private void cookDish(DishCard dishCard) {
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
                        controller.createNewAgent("OperationAgent" + operationCounter.addAndGet(1),
                                OperationAgent.class.getName(),
                                new Object[]{cooker, op.oper_time, processID, dishCard.card_id, cookerID}
                        ).start();
                        operationList.add(op);
                        cookerAgent.CookerOperationCounter.addAndGet(1);
                        System.out.println(myAgent.getName() + ": " + cookerAgent.CookerOperationCounter);
//                        operationCounter = new AtomicInteger(operationCounter.get() + 1);
                        startedOperations.set(true);

                    } catch (StaleProxyException e) {
                        throw new RuntimeException(e);
                    }

//                    operationCounter.addAndGet(1);
//                    System.out.println(myAgent.getName() + ": " + operationCounter.addAndGet(1));
//                    operationCounter = new AtomicInteger(operationCounter.get() + 1);
                }

            }
        }

        @Override
        public boolean done() {
            return false;
        }
    }
}
