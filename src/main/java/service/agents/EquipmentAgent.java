package service.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import service.annotationsetup.SetAnnotationNumber;
import service.models.cooker.Cooker;
import service.models.equipment.Equipment;
import service.util.Theme;

/**
 * Equipment agent. Represents a specific unit of kitchen equipment
 * Possible actions:
 * 1. Subordinates the Cook's Agents
 */
//@JadeAgent(number = 5)
public class EquipmentAgent extends Agent implements SetAnnotationNumber {
    private static String AGENT_TYPE = AgentTypes.EQUIPMENT_AGENT;
    private Equipment myEquipment;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Equipment) {
                myEquipment = (Equipment) args[0];
            }
        }
        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);
//        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " " + myEquipment, Theme.RESET);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentTypes.EQUIPMENT_AGENT);
        sd.setName(AgentTypes.EQUIPMENT_AGENT);
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
        Theme.print(getAID().getName() + " terminating", Theme.RED);
    }

    @Override
    public void setNumber(int number) {
        SetAnnotationNumber.super.setNumber(number);
    }

    private class ListenServer extends Behaviour {
        EquipmentAgent equipmentAgent;

        public ListenServer(EquipmentAgent equipmentAgent) {
            this.equipmentAgent = equipmentAgent;
        }

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
//                if (msg.getOntology().equals(OntologiesTypes.processToCooker)) {
                var dishCardJson = msg.getContent();

                Theme.print(AGENT_TYPE + " " + myAgent.getName() + " got message from " +
                        msg.getSender().getName() + ": " + dishCardJson, Theme.RESET);
                Theme.print(AGENT_TYPE + ": " + getAID().getName() + " " + myEquipment, Theme.RESET);

//                }
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
