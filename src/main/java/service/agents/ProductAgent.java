//package service.agents;
//
//import jade.core.Agent;
//import jade.domain.DFService;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;
//import jade.domain.FIPAException;
//import service.annotationsetup.SetAnnotationNumber;
//import service.behaviour.ReceiveMessageBehaviour;
//import service.util.Theme;
//
//
///**
// * Product agent. Represents a certain amount of product required for a dish.
// * Possible actions:
// * 1. Created by the warehouse agent at the request of the Management agent
// * 2. Reserved at the request of the Warehouse Agent
// * 3. Writes off the number of products from the warehouse after consumption
// */
////@JadeAgent(number = 5)
//public class ProductAgent extends Agent implements SetAnnotationNumber {          // USELESS
//
//    @Override
//    protected void setup() {
//        System.out.println("Hello from " + getAID().getName());
//        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);
//
//        // Register the book-selling service in the yellow pages
//        DFAgentDescription dfd = new DFAgentDescription();
//        dfd.setName(getAID());
//        ServiceDescription sd = new ServiceDescription();
//        sd.setType(AgentTypes.STORAGE_AGENT);
//        sd.setName("JADE-test");
//        dfd.addServices(sd);
//        try {
//            DFService.register(this, dfd);
//        } catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
//
//
//        addBehaviour(new ReceiveMessageBehaviour());
//    }
//    @Override
//    protected void takeDown() {
//        // Deregister from the yellow pages
//        try {
//            DFService.deregister(this);
//        } catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
//        // Print out a dismissal message
//        Theme.print(getAID().getName() + " terminating", Theme.RED);
//    }
//    @Override
//    public void setNumber(int number){
//        SetAnnotationNumber.super.setNumber(number);
//    }
//}
