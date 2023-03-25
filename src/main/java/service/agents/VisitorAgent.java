package service.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import service.annotationsetup.SetAnnotationNumber;
import service.behaviour.MyBehaviour;
import service.configuration.JadeAgent;
import service.models.order.Order;
import service.util.JSONParser;
import service.util.Theme;

/**
 * Visitor's agent.
 * Possible actions:
 * 1. "Ask" Supervisor to create an order
 * 2. Disable an item from the menu if there is no necessary resource
 * 3. Get information about the estimated time of order
 completion * 4. Add/remove an item to/from the order
 * 5. Cancel the order (optional)
 */
@JadeAgent()
public class VisitorAgent extends Agent implements SetAnnotationNumber {
    private final String AGENT_TYPE = AgentTypes.VISITOR_AGENT;
    private final String ONTOLOGY = OntologiesTypes.SUPERVISOR_AGENT;
    private Order order;

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

        addBehaviour(new MyBehaviour(JSONParser.gson.toJson(order), ONTOLOGY, "visitor-supervisor"));
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
}
