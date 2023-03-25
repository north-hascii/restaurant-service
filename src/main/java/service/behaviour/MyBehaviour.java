package service.behaviour;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import service.agents.OntologiesTypes;
import service.util.JsonMessage;

public class MyBehaviour extends Behaviour {
    String message;
    String ontology;
    String serviceType;
    AID name = null;
    Integer id = 0;

    public MyBehaviour(String message, String ontology, String serviceType) {
        this.message = message;
        this.ontology = ontology;
        this.serviceType = serviceType;
    }

    public MyBehaviour(String message, String ontology, String serviceType, Integer id) {
        this.message = message;
        this.ontology = ontology;
        this.serviceType = serviceType;
        this.id = id;
    }

    public MyBehaviour(String message, String ontology, AID name) {
        this.message = message;
        this.ontology = ontology;
        this.name = name;
        if (ontology.equals(OntologiesTypes.OPERATION_COOKER)) {
            System.out.println(name);
        }

    }

    boolean isSend = false;

    @Override
    public void action() {
        JsonMessage cfp = new JsonMessage(ACLMessage.CFP);
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        template.addServices(sd);
        try {
            if (name == null) {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                name = result[id].getName();
            }
            cfp.addReceiver(name);
            cfp.setOntology(ontology);
            cfp.setContent(message);
            myAgent.send(cfp);

            isSend = true;
        } catch (IndexOutOfBoundsException ex) {
            //ex.printStackTrace();
        } catch (Exception ex) {
            try {
//                throw new Error("Cant find agent", ex.getMessage(), ex.getLocalizedMessage());
            } catch (Error ignored) {

            }
        }

    }

    @Override
    public boolean done() {
        return isSend;
    }
}
