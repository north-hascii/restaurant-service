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
import service.behaviour.MyBehaviour;
import service.models.order.Order;
import service.util.info.JSONParser;
import service.util.env.Theme;

import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * Management agent.
 * Possible actions:
 * 1. Receives an order from a Visitor's Agent
 * 2. Creates an Order Agent
 * 3.
 */
//@JadeAgent()
public class SupervisorAgent extends Agent implements SetAnnotationNumber {
    private final String AGENT_TYPE = AgentTypes.SUPERVISOR_AGENT;
    HashMap<String, String> visitorNameAgentNameMap = new HashMap<>();

    @Override
    protected void setup() {
        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        // From where pipe listens (when sending only setType, when receiving setType and setName)
        serviceDescription.setType(AgentTypes.SUPERVISOR_AGENT);
        serviceDescription.setName(AgentTypes.SUPERVISOR_AGENT);

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
                if (msg.getOntology().equals(OntologiesTypes.VISITOR_SUPERVISOR)) {
                    var visitorOrderJSON = msg.getContent();
                    var visitorOrder = JSONParser.gson.fromJson(visitorOrderJSON, Order.class);
                    visitorNameAgentNameMap.put(visitorOrder.vis_name, msg.getSender().getName());

                    Theme.print(AGENT_TYPE + " " + myAgent.getName() +
                            " got message from " + msg.getSender().getName() + ": " + visitorOrderJSON, Theme.RESET
                    );

                    String supervisorAgentName = getName();

                    // Read the message
                    var controller = myAgent.getContainerController();
                    try {
                        controller.createNewAgent("OrderAgent" + orderCounter,
                                OrderAgent.class.getName(),
                                new Object[]{visitorOrder, supervisorAgentName}
                        ).start();

                        orderCounter++;
                    } catch (StaleProxyException e) {
                        throw new RuntimeException(e);
                    }

                } else if (msg.getOntology().equals(OntologiesTypes.ORDER_SUPERVISOR)) {
                    var visitorOrderJSON = msg.getContent();
                    var visitorOrder = JSONParser.gson.fromJson(visitorOrderJSON, Order.class);

                    Theme.print(AGENT_TYPE + " " + myAgent.getName() +
                            " got message from " + msg.getSender().getName() + ": " + visitorOrderJSON, Theme.BLUE
                    );

                    myAgent.addBehaviour(new MyBehaviour(JSONParser.gson.toJson(visitorOrder),
                            OntologiesTypes.SUPERVISOR_VISITOR,
                            AgentTypes.VISITOR_AGENT,
                            visitorNameAgentNameMap.get(visitorOrder.vis_name))
                    );

                } else if (msg.getOntology().equals(OntologiesTypes.VISITOR_DELETE_SUPERVISOR)) {
                    var visitorOrderJSON = msg.getContent();

                    Theme.print(AGENT_TYPE + " " + myAgent.getName() +
                            " got message from " + msg.getSender().getName() + ": " + visitorOrderJSON, Theme.BLUE
                    );
//                    System. exit(0);
                    myAgent.addBehaviour(new MyBehaviour("Delete storage",
                            OntologiesTypes.SUPERVISOR_DELETE_STORAGE,
                            AgentTypes.STORAGE_AGENT)
                    );
//                    myAgent.doDelete();
                }


            } else {
                block();
            }
        }
    }
}
