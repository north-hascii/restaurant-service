package service.behaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import service.util.JsonMessage;

public class MyTickerBehaviour extends TickerBehaviour {
    String message;
    String ontology;
    String agentType;
    AID name = null;

    public MyTickerBehaviour(Agent agent, long period, String message, String ontology, String agentType) {
        super(agent, period);
        this.message = message;
        this.ontology = ontology;
        this.agentType = agentType;
    }

    @Override
    protected void onTick() {
        JsonMessage cfp = new JsonMessage(ACLMessage.CFP);
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(agentType);
        template.addServices(sd);
        try {
            if (name == null) {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                name = result[0].getName();
            }
            cfp.addReceiver(name);
            cfp.setOntology(ontology);
            cfp.setContent(message);
            myAgent.send(cfp);
//            isSend = true;
        } catch (IndexOutOfBoundsException ex) {
            //ex.printStackTrace();
        } catch (Exception ex) {
            try {
//                throw new Error("Cant find agent", ex.getMessage(), ex.getLocalizedMessage());
            } catch (Error ignored) {

            }
        }

    }
}
