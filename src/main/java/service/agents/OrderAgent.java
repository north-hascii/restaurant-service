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
import service.models.dishCard.DishCardList;
import service.models.order.Order;
import service.util.info.JSONParser;
import service.util.env.Theme;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Order agent. Reports to the Management Agent.
 * Possible actions:
 * 1. The agent receives an order from the Supervisor
 * 2. Sends the order for verification to the Storage Agent
 * 3. Creates a process agent for each dish
 * 4. Dies at the end of the process
 */
//@JadeAgent(number = 1)
public class OrderAgent extends Agent implements SetAnnotationNumber {
    private final String AGENT_TYPE = AgentTypes.ORDER_AGENT;

    private Order order;
    private DishCardList dishCardList = null;

    private static final AtomicInteger processCounter = new AtomicInteger(0);
    private String supervisorAgentName;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Order) {
                order = (Order) args[0];
            }
            if (args[1] instanceof String) {
                supervisorAgentName = (String) args[1];
            }
        }

        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);
        Theme.print(AGENT_TYPE + "Got order: " + JSONParser.gson.toJson(order), Theme.RESET);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        // From where pipe listens (when sending only setType, when receiving setType and setName)
        serviceDescription.setType(AgentTypes.ORDER_AGENT);
        serviceDescription.setName(AgentTypes.ORDER_AGENT);

        dfd.addServices(serviceDescription);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new MyBehaviour(JSONParser.gson.toJson(order), OntologiesTypes.ORDER_STORAGE, AgentTypes.STORAGE_AGENT));

        // Set up Listener
        addBehaviour(new ListenServer(this));
    }

    private void createProcessAgents() {
        var controller = getContainerController();
        for (var dishCard : dishCardList.dish_cards) {
            try {
                var processID = processCounter.addAndGet(1);
                controller.createNewAgent("ProcessAgent" + processID,
                        ProcessAgent.class.getName(),
                        new Object[]{processID, dishCard, getName()}
                ).start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        }
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
        Theme.print(getAID().getName() + " terminating", Theme.RED);
    }

    @Override
    public void setNumber(int number) {
        SetAnnotationNumber.super.setNumber(number);
    }

    private class ListenServer extends Behaviour {
        OrderAgent orderAgent;

        public ListenServer(OrderAgent orderAgent) {
            this.orderAgent = orderAgent;
        }

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getOntology().equals(OntologiesTypes.STORAGE_ORDER)) {
                    var dishCardsJson = msg.getContent();
                    Theme.print(AGENT_TYPE + " " + myAgent.getName() +
                            " got message from " + msg.getSender().getName() + ": " + dishCardsJson, Theme.RESET
                    );
                    dishCardList = JSONParser.gson.fromJson(dishCardsJson, DishCardList.class);
//                    System.out.println(dishCardList);
                    createProcessAgents();
                } else if (msg.getOntology().equals(OntologiesTypes.PROCESS_ORDER)) {
                    var json = msg.getContent();
                    Theme.print(AGENT_TYPE + " " + myAgent.getName() +
                            " got message from " + msg.getSender().getName() + ": " + json, Theme.RESET
                    );

                    addBehaviour(new MyBehaviour("Delete process.",
                            OntologiesTypes.ORDER_DELETE_PROCESS,
                            msg.getSender())
                    );

                    addBehaviour(new MyBehaviour(JSONParser.gson.toJson(order),
                            OntologiesTypes.ORDER_SUPERVISOR,
                            AgentTypes.SUPERVISOR_AGENT,
                            supervisorAgentName)
                    );
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
