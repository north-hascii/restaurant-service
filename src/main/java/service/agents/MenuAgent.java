//package service.agents;
//
//import jade.core.Agent;
//import jade.core.behaviours.CyclicBehaviour;
//import jade.domain.DFService;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;
//import jade.domain.FIPAException;
//import jade.lang.acl.ACLMessage;
//import service.Main;
//import service.annotationsetup.SetAnnotationNumber;
//import service.behaviour.ReceiveMessageBehaviour;
//import service.configuration.JadeAgent;
//import service.models.dishCard.DishCard;
//import service.models.operation.Operation;
//
//import java.util.ArrayList;
//
////@JadeAgent(number = 5)
//public class MenuAgent extends Agent implements SetAnnotationNumber {         // USELESS !
//    private final String AGENT_TYPE = AgentTypes.storageAgent;
//    private final String ONTOLOGY = "1234567";
//
//    @Override
//    protected void setup() {
//        System.out.println(AGENT_TYPE + ": " + getAID().getName() + " is ready.");
//
//        DFAgentDescription dfd = new DFAgentDescription();
//        dfd.setName(getAID());
//
//        ServiceDescription serviceDescription = new ServiceDescription();
//        // From where pipe listens (when sending only setType, when receiving setType and setName)
//        serviceDescription.setType("supervisor-menu");
//        serviceDescription.setName("supervisor-menu");
//
//        dfd.addServices(serviceDescription);
//
//        try {
//            DFService.register(this, dfd);
//        } catch (FIPAException ex) {
//            ex.printStackTrace();
//        }
//
//        // Assign a Listener
//        addBehaviour(new MenuAgent.ListenServer());
//    }
//
//    @Override
//    protected void takeDown() {
//        // Deregister from the yellow pages
//        try {
//            DFService.deregister(this);
//        } catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
//        // Print out a dismissal message
//        System.out.println("testAgent " + getAID().getName() + " terminating");
//    }
//    @Override
//    public void setNumber(int number){
//        SetAnnotationNumber.super.setNumber(number);
//    }
//
//    private class ListenServer extends CyclicBehaviour {
//
//        public void action() {
//            // Receives a message
//            ACLMessage msg = myAgent.receive();
//            if (msg != null) {
//                // Received data about the Dish for cooking
//                var visitorMessage = msg.getContent(); // - null
//                if (Main.menuInfo >= 1) {
//                    System.out.println(myAgent.getName().split("@")[0] + " got request from " +
//                            msg.getSender().getName().split("@")[0]);
//                }
//                ArrayList<DishCard> verifiedDishes = verifyDish();
//
//                // RETURN LIST TO SERVICE
//
////                var controller = myAgent.getContainerController();
////                var i = Order.index.get();
////                try {
////                    controller.createNewAgent("OrderAgent" + i, OrderAgent.class.getName(), null);
////                    Order.index = new AtomicInteger(i + 1);
////                } catch (StaleProxyException e) {
////                    throw new RuntimeException(e);
////                }
//
////                myAgent.addBehaviour(new MyBehaviour(visitorMessage, ONTOLOGY, "supervisor-storage"));
//            } else {
//                block();
//            }
//        }
//
//        private ArrayList<DishCard> verifyDish() {
//            ArrayList<DishCard> verifiedDishes = new ArrayList<>();
//
//            ArrayList<DishCard> allDishes = (ArrayList<DishCard>) Main.db.dishCardsMap.values();
//
//            System.out.println("allDishes " + allDishes);
//
//            return verifiedDishes;
//        }
//    }
//}
