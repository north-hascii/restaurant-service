package service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import service.models.cooker.CookerList;
import service.models.dishCard.DishCardList;
import service.models.equipment.EquipmentList;
import service.models.equipment.EquipmentTypeList;
import service.models.menuItem.MenuItemList;
import service.models.operation.OperationTypesList;
import service.models.order.Order;
import service.models.order.OrderList;
import service.models.product.ProductInStorageList;
import service.models.product.ProductTypesList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

public class JSONParser<T> {
    public static final com.google.gson.Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private static final String filePath = "database";

    public JSONParser() {
    }

    public static String readDataFromJsonFile(String filePath) {
        String data = null;
        try {
            data = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("Error while reading data from file: " + filePath + " | " + e.getMessage());
        }
        return data;
    }

    public static <T> T parseJson(final TypeReference<T> type, final String json) {
        T data = null;
        try {
            data = new ObjectMapper().readValue(json, type);
//            System.out.println(data);
        } catch (Exception e) {
            System.out.println("Error while parsing json: " + e.getMessage());
        }
        return data;
    }


    public static <T> T fromJSONMapper(final TypeReference<T> type, final String json) {
        T data = null;
        try {
            data = new ObjectMapper().readValue(json, type);
        } catch (Exception e) {
            // Handle the problem
        }
        return data;
    }


    public MenuItemList parseMenuItemList(String filePath) {
        MenuItemList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<MenuItemList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    public DishCardList parseDishCardsList(String filePath) {
        DishCardList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<DishCardList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }


    public ProductTypesList parseProductTypesList(String filePath) {
        ProductTypesList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<ProductTypesList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }


    public ProductInStorageList parseProductOnStockList(String filePath) {
        ProductInStorageList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<ProductInStorageList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }


    public EquipmentList parseEquipmentList(String filePath) {
        EquipmentList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<EquipmentList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }


    public EquipmentTypeList parseEquipmentTypeList(String filePath) {
        EquipmentTypeList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<EquipmentTypeList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    public CookerList parseCookerList(String filePath) {
        CookerList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<CookerList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    public OperationTypesList parseOperationTypesList(String filePath) {
        OperationTypesList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<OperationTypesList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }


    public OrderList parseOrderList(String filePath) {
        OrderList data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSONParser.filePath + "/" + filePath)));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<OrderList> typeReference = new TypeReference<>() {
            };
            data = mapper.readValue(content, typeReference);

            data.visitors_orders.sort(new Comparator<Order>() {
                @Override
                public int compare(Order m1, Order m2) {
                    return m1.vis_ord_started.compareTo(m2.vis_ord_started);
                }
            });
            System.out.println(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }


}
