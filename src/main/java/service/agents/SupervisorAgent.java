package service.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import service.annotationsetup.SetAnnotationNumber;
import service.models.order.Order;
import service.util.JSONParser;
import service.util.Theme;

import static java.lang.Thread.sleep;

/**
 * Management agent.
 * Possible actions:
 * 1. Starts the process of creating a new Order Agent
 * 2. Controls the destruction of the order upon its completion
 * 3. "Orders" the warehouse Agent to reserve products for each dish
 * 4. "Orders" the warehouse Agent to cancel the reservation of products for dishes
 * 5. Processes the response of the Warehouse Agent
 * 6. ? Creates and destroys other agents
 */
//@JadeAgent(number = 1)
public class SupervisorAgent extends Agent implements SetAnnotationNumber {
    private final String AGENT_TYPE = AgentTypes.SUPERVISOR_AGENT;
    private final String ONTOLOGY = OntologiesTypes.SUPERVISOR_AGENT;

    @Override
    protected void setup() {
        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        // From where pipe listens (when sending only setType, when receiving setType and setName)
        serviceDescription.setType("visitor-supervisor");
        serviceDescription.setName("visitor-supervisor");

        dfd.addServices(serviceDescription);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        // Set up Listener
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

    private class ListenServer extends CyclicBehaviour {
        private static Integer orderCounter = 0;

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                var visitorOrderJSON = msg.getContent();
                var visitorOrder = JSONParser.gson.fromJson(visitorOrderJSON, Order.class);

                Theme.print(AGENT_TYPE + " " + myAgent.getName() + " got message from " +
                        msg.getSender().getName() + ": " + visitorOrderJSON, Theme.RESET);

                // Read the message
                var controller = myAgent.getContainerController();
                try {
                    controller.createNewAgent("OrderAgent" + orderCounter, OrderAgent.class.getName(),
                            new Order[]{visitorOrder}).start();
                    orderCounter++;
                } catch (StaleProxyException e) {
                    throw new RuntimeException(e);
                }

                // New Agent Order by pipe supervisor-order with the value visitor_order
//                myAgent.addBehaviour(new MyBehaviour(visitorOrderJSON, ONTOLOGY, "supervisor-storage"));

            } else {
                block();
            }
        }
    }
}
