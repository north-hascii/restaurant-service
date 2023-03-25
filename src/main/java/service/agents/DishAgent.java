package service.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import service.annotationsetup.SetAnnotationNumber;
import service.util.Theme;

/**
 * Agent of the dish/drink. Contains lists of process agents and operations.
 * Possible actions:
 * 1. It is destroyed after the order is completed.
 */
//@JadeAgent(number = 5)
public class DishAgent extends Agent implements SetAnnotationNumber {
    private final String AGENT_TYPE = AgentTypes.MENU_AGENT;

    @Override
    protected void setup() {
        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("supervisor-order");
        serviceDescription.setName("supervisor-order");

        dfd.addServices(serviceDescription);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            ex.printStackTrace();
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
}
