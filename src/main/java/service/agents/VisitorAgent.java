package service.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import service.Main;
import service.annotationsetup.SetAnnotationNumber;
import service.behaviour.MyBehaviour;
import service.configuration.JadeAgent;
import service.models.dishCard.DishCardList;
import service.models.order.Order;
import service.util.JSONParser;
import service.util.Theme;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Visitor's agent.
 * Possible actions:
 * 1. "Ask" Supervisor to create an order
 * 2. Disable an item from the menu if there is no necessary resource
 * 3. Get information about the estimated time of order
 * completion * 4. Add/remove an item to/from the order
 * 5. Cancel the order (optional)
 */
@JadeAgent()
public class VisitorAgent extends Agent implements SetAnnotationNumber {
    private final String AGENT_TYPE = AgentTypes.VISITOR_AGENT;
    private final String ONTOLOGY = OntologiesTypes.SUPERVISOR_AGENT;
    private Order order;
    private static AtomicInteger visitorCounter = new AtomicInteger(Main.db.getOrderList().visitors_orders.size());

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Order) {
                order = (Order) args[0];
            }
        }

        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);
        Theme.print(AGENT_TYPE + "Got order: " + JSONParser.gson.toJson(order), Theme.RESET);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        // From where pipe listens (when sending only setType, when receiving setType and setName)
        serviceDescription.setType(AgentTypes.VISITOR_AGENT);
        serviceDescription.setName(AgentTypes.VISITOR_AGENT);

        dfd.addServices(serviceDescription);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new MyBehaviour(JSONParser.gson.toJson(order), OntologiesTypes.VISITOR_SUPERVISOR, AgentTypes.SUPERVISOR_AGENT));
        addBehaviour(new ListenServer());
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
//        OrderAgent orderAgent;
//
//        public ListenServer(OrderAgent orderAgent) {
//            this.orderAgent = orderAgent;
//        }

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getOntology().equals(OntologiesTypes.SUPERVISOR_VISITOR)) {
                    visitorCounter.decrementAndGet();
                    if (visitorCounter.get() == 0) {
                        myAgent.addBehaviour(new MyBehaviour("Delete supervisitor", OntologiesTypes.VISITOR_DELETE_SUPERVISOR, msg.getSender()));
                        Theme.print("!!!!!!!!!! ALL ORDERS ARE READY", Theme.GREEN);
                    }

                    var dishCardsJson = msg.getContent();
                    Theme.print(AGENT_TYPE + " " + myAgent.getName() + " got message from " + msg.getSender().getName() + ": " + dishCardsJson, Theme.PURPLE);
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
