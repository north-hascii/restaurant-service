package service.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import service.Main;
import service.annotationsetup.SetAnnotationNumber;
import service.behaviour.MyBehaviour;
import service.configuration.JadeAgent;
import service.models.dishCard.DishCard;
import service.models.dishCard.DishCardList;
import service.models.menuItem.MenuItem;
import service.models.operation.Operation;
import service.models.operation.ProductInfoInOperation;
import service.models.order.Order;
import service.models.order.OrderItem;
import service.models.product.ProductInStorage;
import service.util.info.JSONParser;
import service.util.env.Theme;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Agent Warehouse.
 * Possible actions:
 * 1. The Agent receives an order from the Order Agent and analyzes the dishes, returns a list of possible dishes
 */
@JadeAgent(number = 1)
public class StorageAgent extends Agent implements SetAnnotationNumber {
    private final String AGENT_TYPE = AgentTypes.STORAGE_AGENT;

    @Override
    protected void setup() {
        System.out.println(AGENT_TYPE + ": " + getAID().getName() + " is ready.");

        Theme.print(AGENT_TYPE + ": " + getAID().getName() + " is ready.", Theme.GREEN);
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        // From where pipe listens (when sending only setType, when receiving setType and setName)
        serviceDescription.setType(AgentTypes.STORAGE_AGENT);
        serviceDescription.setName(AgentTypes.STORAGE_AGENT);

        dfd.addServices(serviceDescription);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        if (Main.storageInfo >= 2) {
            System.out.println("------------------\n" +
                    "Main.db.dishCardsMap " + Main.db.dishCardsMap + "\n" +
                    "Main.db.menuMap " + Main.db.menuMap + "\n" +
                    "Main.db.productInStorageMap " + Main.db.productInStorageMap + "\n" +
                    "------------------");
        }

        // Assign a Listener
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

    private class ListenServer extends CyclicBehaviour {
        private StorageAgent agent;

        public ListenServer(StorageAgent agent) {
            this.agent = agent;
        }

        public void action() {
            // Receives a message
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getOntology().equals(OntologiesTypes.ORDER_STORAGE)) {
                    // Received data about the Dish for cooking
                    var visitorMessage = msg.getContent();
                    if (Main.storageInfo >= 1) {
                        System.out.println(myAgent.getName().split("@")[0] + " got message from " +
                                msg.getSender().getName().split("@")[0] + ": " + visitorMessage);
                    }

                    DishCardList verifiedDishCardList = new DishCardList(verifyOrder(visitorMessage));

                    if (Main.storageInfo >= 3) {
                        System.out.println("verifiedDishCardList " + verifiedDishCardList);
                    }
                    myAgent.addBehaviour(new MyBehaviour(JSONParser.gson.toJson(verifiedDishCardList),
                            OntologiesTypes.STORAGE_ORDER, msg.getSender()));
                } else if (msg.getOntology().equals(OntologiesTypes.SUPERVISOR_DELETE_STORAGE)) {
                    Theme.print("Storage receives the command to die", Theme.BLUE);
                    myAgent.doDelete();
                }

            } else {
                block();
            }
        }

        public ArrayList<DishCard> verifyOrder(String visitorMessage) {
            // Get the order
            Order order = JSONParser.gson.fromJson(visitorMessage, Order.class);
            ArrayList<OrderItem> orderItems = order.vis_ord_dishes;

            ArrayList<DishCard> orderVerifiedOperations = new ArrayList<DishCard>();

            if (Main.storageInfo >= 2) {
                System.out.println("\n-----------Order for " + order.vis_name + ". Verification STARTED-----------");

                if (Main.storageInfo >= 3) {

                    StringBuilder storageContent = new StringBuilder("-----Storage content------");
                    int i = 1;
                    for (var item : Main.db.productInStorageMap.values()) {
                        storageContent.
                                append("\n\t").
                                append(i++).
                                append(". ").
                                append(item.prod_item_name).
                                append(" - ").
                                append(item.prod_item_quantity);
                    }
                    storageContent.append("\n-------------------------");
                    System.out.println(storageContent);
                }

                int j = 1;
                StringBuilder orderContent = new StringBuilder("-----Order content------");
                for (OrderItem item : orderItems) {
                    orderContent.
                            append("\n\t").
                            append(j++).
                            append(". ").
                            append(Main.db.dishCardsMap.get(Main.db.menuMap.get(item.menu_dish).menu_dish_card).dish_name);
                }
                orderContent.append("\n------------------------");
                System.out.println(orderContent);
            }

            for (OrderItem item : orderItems) {
                int dishMenuIndex = item.menu_dish;

                MenuItem menuItem = Main.db.menuMap.get(dishMenuIndex);

                DishCard dishRecipe = Main.db.dishCardsMap.get(menuItem.menu_dish_card);

                if (dishRecipe == null) {
                    continue;
                }

                boolean isTypeExist = true;
                var keySet = Main.db.equipmentMap.keySet();
                for (var key : keySet) {
                    if (key == dishRecipe.equip_type) {
                        isTypeExist = false;
                        break;
                    }
                }
                if (isTypeExist) {
                    continue;
                }

//                var neededEquipment = Main.db.equipmentMap.get(dishRecipe.equip_type);

                boolean isAllOperationsPossible = true;

                var operations = dishRecipe.operations;

                HashMap<Integer, Double> mapOfProducts = new HashMap<>();

                for (Operation operation : operations) {
                    for (ProductInfoInOperation neededProduct : operation.oper_products) {
                        // I APPLY BY TYPE, NOT BY ID
                        // If there are several ids with the same type, it will not work
                        int productType = neededProduct.prod_type;
                        double prodQuantity = neededProduct.prod_quantity;

                        ProductInStorage currentProduct = Main.db.productInStorageMap.get(productType);
                        if (currentProduct == null) {
                            continue;
                        }

                        double inStorage = currentProduct.prod_item_quantity;

                        if (inStorage >= prodQuantity && inStorage - prodQuantity >= 0) {
                            // EVERYTHING is OK, the product is there, we reserve
                            mapOfProducts.put(productType, prodQuantity);
//                            currentProduct.prod_item_quantity = inStorage - prodQuantity;
//                            Main.db.productInStorageMap.put(productType, currentProduct);
                            if (Main.storageInfo >= 2) {
                                System.out.println("OK, product " + currentProduct.prod_item_name + " reserved. " +
                                        "Left in the store " + currentProduct.prod_item_quantity);
                            }
                        } else {
                            // EVERYTHING is not ok, there is no product, WE do NOT reserve
                            isAllOperationsPossible = false;
//                            if (Main.storageInfo >= 2) {

                            Theme.print("NOT OK, " +
                                    "product " + currentProduct.prod_item_name +
                                    " for " + dishRecipe.dish_name + " for " + order.vis_name + " skipped. " +
                                    "Left in the store " + currentProduct.prod_item_quantity, Theme.YELLOW);
//                            }
                        }
                    }
                }
                if (isAllOperationsPossible) {
                    orderVerifiedOperations.add(dishRecipe);
                    // subtract the necessary resources
                    for (var productType : mapOfProducts.keySet()) {
                        ProductInStorage currentProduct = Main.db.productInStorageMap.get(productType);
                        double inStorage = currentProduct.prod_item_quantity;
                        currentProduct.prod_item_quantity = inStorage - mapOfProducts.get(productType);
                        Main.db.productInStorageMap.put(productType, currentProduct);
                    }
                }
            }
            if (Main.storageInfo >= 2) {
                System.out.println("-----------Order for " + order.vis_name + ". Verification ENDED-----------");
            }

            return orderVerifiedOperations;
        }
    }
}
