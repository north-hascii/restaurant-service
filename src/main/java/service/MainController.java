package service;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.reflections.Reflections;
import service.agents.*;
import service.configuration.JadeAgent;
import service.models.cooker.Cooker;
import service.models.equipment.Equipment;
import service.models.order.Order;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;

class MainController {

    private final ContainerController containerController;

    public MainController() {
        final Runtime rt = Runtime.instance();
        final Profile p = new ProfileImpl();

        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "8083");
        p.setParameter(Profile.GUI, "true");

        containerController = rt.createMainContainer(p);
        System.out.println(containerController);
    }

    void initAgents() {
        try {
            createVisitorAgent();
            createSupervisorAgent();
            createStorageAgent();
            createCookerAgent();
            createEquipmentAgent();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


//        initAgents(MainController.class.getPackageName());
    }

    private void createVisitorAgent() throws StaleProxyException, Error {
        int counter = 0;
        for (var order : Main.db.getOrderList().visitors_orders) {
            containerController.createNewAgent("VisitorAgent" + counter, VisitorAgent.class.getName(), new Order[]{order}).start();
            counter += 1;
        }
    }

    private void createSupervisorAgent() throws StaleProxyException, Error {
        containerController.createNewAgent("SupervisorAgent", SupervisorAgent.class.getName(), null).start();
    }

    private void createStorageAgent() throws StaleProxyException, Error {
        containerController.createNewAgent("StorageAgent", StorageAgent.class.getName(), null).start();
    }

    private void createCookerAgent() throws StaleProxyException, Error {
        int counter = 0;
        for (var cooker : Main.db.getCookersList().cookers) {
            containerController.createNewAgent("CookerAgent" + counter, CookerAgent.class.getName(), new Object[]{cooker, counter}).start();
            counter += 1;
        }
    }

    private void createEquipmentAgent() throws StaleProxyException, Error {
        int counter = 0;
        for (var equipment : Main.db.getEquipmentList().equipment) {
            containerController.createNewAgent("EquipmentAgent" + counter, EquipmentAgent.class.getName(), new Equipment[]{equipment}).start();
            counter += 1;
        }
    }

}
