package service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import service.Main;
import service.models.MyErrorInner;
import service.models.dishCard.DishCard;
import service.models.MyError;
import service.models.operation.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class Configurator {

    String cookersFileName = "cookers.json";
    String dishCardsFileName = "dish_cards.json";
    String equipmentFileName = "equipment.json";
    String equipmentTypeFileName = "equipment_type.json";
    String menuDishesFileName = "menu_dishes.json";
    String operationLogFileName = "operation_log.json";
    String operationTypesFileName = "operation_types.json";
    String processLogFileName = "process_log.json";
    String productTypesFileName = "product_types.json";
    String productsFileName = "products.json";
    String visitorsOrdersFileName = "visitors_orders.json";

    String mainFolder = "database/";
//    String mainFolder = "src/main/resources/database/";

    public Configurator() {

    }


// Doesn't work :(
//    ArrayList<Function<SimpleList, String>> operations = new ArrayList<>();

    public boolean setUpDataBase() {
        boolean result = true;

        Main.db = new DataBase();

        try {
            Main.db.setCookersList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + cookersFileName)));

            Main.db.setDishCardsList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + dishCardsFileName)));

            Main.db.setEquipmentList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + equipmentFileName)));

            Main.db.setEquipmentTypesList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + equipmentTypeFileName)));

            Main.db.setMenuList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + menuDishesFileName)));

            Main.db.setOperationTypesList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + operationTypesFileName)));

            Main.db.setProductTypesList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + productTypesFileName)));

            Main.db.setProductInStorageList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + productsFileName)));

            Main.db.setOrderList(JSONParser.parseJson(new TypeReference<>() {
            }, JSONParser.readDataFromJsonFile(mainFolder + visitorsOrdersFileName)));

//            Theme.print(Main.db.toString(), Theme.RESET);

        } catch (Exception e) {
            Theme.print("ERROR While reading JSONs from database folder!: " + e.getMessage(), Theme.RED);
            MyError myError = new MyError(
                    e.getClass().getTypeName(),
                    e.getMessage(),
                    "ERROR While reading JSONs from database folder"
            );
            MyErrorInner myErrorInner = new MyErrorInner(myError);

            MyLog.LogMyError(JSONParser.gson.toJson(myErrorInner));
            result = false;
        }

        int errorOnAsync = 0;
        try {
            ArrayList<DishCard> dishCards = Main.db.getDishCardsList().dish_cards;
            for (DishCard dish : dishCards) {
                ArrayList<Operation> operations = dish.operations;

                HashMap<Integer, Integer> hashMap = new HashMap<>();

                for (Operation operation : operations) {
                    if (hashMap.containsKey(operation.oper_async_point)) {
                        hashMap.put(operation.oper_async_point, hashMap.get(operation.oper_async_point) + 1);
                    } else {
                        hashMap.put(operation.oper_async_point, 1);
                    }
                }

                if (hashMap.size() > 0) {
                    for (var item : hashMap.keySet()) {
                        if (item != 0 && hashMap.get(item) == 1) {
                            errorOnAsync = item;
                            throw new IllegalArgumentException("DishCard " + dish.dish_name +
                                    " does not have the value " + errorOnAsync
                                    + " for oper_async_point"
                            );
                        }
                    }
                }
            }
        } catch (Exception e) {
            Theme.print("ERROR While checking DishCards data!: " + e.getMessage(), Theme.RED);
            MyError myError = new MyError(
                    e.getClass().getTypeName(),
                    e.getMessage(),
                    Integer.toString(errorOnAsync)
            );
            MyErrorInner myErrorInner = new MyErrorInner(myError);

            MyLog.LogMyError(JSONParser.gson.toJson(myErrorInner));
            result = false;
        }

        return result;
    }

    @Override
    public String toString() {
        return "Configurator{" +
                "cookersFileName='" + cookersFileName + '\'' +
                ", dishCardsFileName='" + dishCardsFileName + '\'' +
                ", equipmentFileName='" + equipmentFileName + '\'' +
                ", equipmentTypeFileName='" + equipmentTypeFileName + '\'' +
                ", menuDishesFileName='" + menuDishesFileName + '\'' +
                ", operationLogFileName='" + operationLogFileName + '\'' +
                ", operationTypesFileName='" + operationTypesFileName + '\'' +
                ", processLogFileName='" + processLogFileName + '\'' +
                ", productTypesFileName='" + productTypesFileName + '\'' +
                ", productsFileName='" + productsFileName + '\'' +
                ", visitorsOrdersFileName='" + visitorsOrdersFileName + '\'' +
                '}';
    }
}
