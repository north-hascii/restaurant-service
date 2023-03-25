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
import service.models.cooker.Cooker;
import service.models.dishCard.DishCard;
import service.models.equipment.Equipment;
import service.models.operation.Operation;
import service.models.operation.OperationList;
import service.models.operation.OperationLog;
import service.models.operation.OperationTypesList;
import service.models.process.ProcessLog;
import service.models.process.ProcessOperation;
import service.util.JSONParser;
import service.util.MyLog;
import service.util.Theme;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Process agent. Performs itself using Agents of Operations using cooks or kitchen equipment and cooks.
 * Possible actions:
 * 1. "Communicates" with the Agents of operations
 * 2. It is destroyed after the order is completed.
 */
//@JadeAgent(number = 5)
public class ProcessAgent extends Agent implements SetAnnotationNumber {
    private static String AGENT_TYPE = AgentTypes.PROCESS_AGENT;

    private Integer processID = 0;
    private DishCard dishCard;

    private Date begin;
    private Date end;
    private Equipment selectedEquipment;
    private Cooker selectedCooker;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Integer) {
                processID = (Integer) args[0];
            }
            if (args[1] instanceof DishCard) {
                dishCard = (DishCard) args[1];
            }
        }
        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);
        Theme.print(AGENT_TYPE + "Got order: " + JSONParser.gson.toJson(dishCard), Theme.RESET);

        // Register the book-selling service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentTypes.PROCESS_AGENT);
        sd.setName(AgentTypes.PROCESS_AGENT);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        begin = new Date();

        prepareCookerAndEquipment();

        addBehaviour(new ListenServer());
    }

    private final Object monitor = new Object();

    private void prepareCookerAndEquipment() {

        synchronized (monitor) {
            Boolean isCookerReady = false;
            Boolean isEquipmentReady = false;
//        Cooker selectedCooker = null;
//        Equipment selectedEquipment = null;
            // if equip_type is null
            if (dishCard.equip_type == 0) {
                isEquipmentReady = true;
                selectedEquipment = new Equipment();
            }
            Theme.print(getAID().getName() + " started preparing...", Theme.YELLOW);

            int cookerDBId = 0;
            int equipmentId = 0;
            while (!isCookerReady || !isEquipmentReady) {
                cookerDBId = 0;
                for (var cooker : Main.db.getCookersList().cookers) {
                    if (!cooker.cook_active) {
                        isCookerReady = true;
                        selectedCooker = cooker;
                        break;
                    }
                    cookerDBId++;
                }
                equipmentId = 0;
                for (var eq : Main.db.getEquipmentList().equipment) {
                    if (!eq.equip_active.get() && eq.equip_type == dishCard.equip_type) {
                        isEquipmentReady = true;
                        selectedEquipment = eq;
                        break;
                    }
                    equipmentId++;
                }
                if (isCookerReady && isEquipmentReady) {
                    Theme.print(getAID().getName() + " finished preparing...", Theme.YELLOW);
                    DataFromProcess dataFromProcess = new DataFromProcess(processID, dishCard, getName());
                    selectedCooker.cook_active = true;
//                selectedEquipment.equip_active = true;
                    selectedEquipment.equip_active.set(true);
                    Theme.print(getName() + " chose: " + selectedCooker + " " + selectedEquipment, Theme.BLUE);
                    addBehaviour(new MyBehaviour(JSONParser.gson.toJson(dataFromProcess), OntologiesTypes.PROCESS_COOKER, AgentTypes.COOKER_AGENT, cookerDBId));
                    addBehaviour(new MyBehaviour(JSONParser.gson.toJson(dishCard), OntologiesTypes.PROCESS_EQUIPMENT, AgentTypes.EQUIPMENT_AGENT, equipmentId));
                }
            }
        }

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

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getOntology().equals(OntologiesTypes.COOKER_PROCESS)) {
                    Main.db.equipmentMap.get(selectedEquipment.equip_type);
//                    selectedEquipment.equip_active = false;
                    selectedEquipment.equip_active.set(false);
                    selectedCooker.cook_active = false;
                    var json = msg.getContent();
                    System.out.println("LOG" + AGENT_TYPE + " " + myAgent.getName() + " got message from " + msg.getSender().getName() + ": " + json);
                    OperationIdList list = JSONParser.gson.fromJson(json, OperationIdList.class);

                    end = new Date();

//                    Theme.print("selectedEq: " + selectedEquipment.toString(), Theme.BLUE);
//                    selectedEquipment.equip_name = "PIZDA";
//                    Theme.print("selectedEq: " + selectedEquipment.toString(), Theme.BLUE);
                    ArrayList<ProcessOperation> processOperationList = new ArrayList<>();
                    for (var operationId : list.operationIdList) {
                        processOperationList.add(new ProcessOperation(operationId));
                    }

                    ProcessLog processLog = new ProcessLog(
                            processID, dishCard.card_id, begin, end, false, processOperationList
                    );

                    MyLog.LogProcess(JSONParser.gson.toJson(processLog));

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
