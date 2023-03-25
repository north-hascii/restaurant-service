package service.util;

import service.models.cooker.Cooker;
import service.models.cooker.CookerList;
import service.models.dishCard.DishCard;
import service.models.equipment.Equipment;
import service.models.equipment.EquipmentList;
import service.models.equipment.EquipmentType;
import service.models.equipment.EquipmentTypeList;
import service.models.dishCard.DishCardList;
import service.models.menuItem.MenuItem;
import service.models.menuItem.MenuItemList;
import service.models.operation.OperationLog;
import service.models.operation.OperationLogsList;
import service.models.operation.OperationType;
import service.models.operation.OperationTypesList;
import service.models.order.Order;
import service.models.order.OrderList;
import service.models.product.ProductInStorage;
import service.models.product.ProductInStorageList;
import service.models.product.ProductType;
import service.models.product.ProductTypesList;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DataBase {
    private CookerList cookerList;
    private EquipmentList equipmentList;
    private EquipmentTypeList equipmentTypeList;
    private DishCardList dishCardList;
    private MenuItemList menuList;
    private OperationTypesList operationTypesList;
    private OperationLogsList operationLogsList;
    private OrderList orderList;
    private ProductInStorageList productInStorageList;
    private ProductTypesList productTypesList;

    public ConcurrentHashMap<Integer, Cooker> cookerMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, ArrayList<Equipment>> equipmentMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, EquipmentType> equipmentTypeMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, DishCard> dishCardsMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, MenuItem> menuMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, OperationType> operationTypesMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, OperationLog> operationLogsMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Order> orderMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, ProductInStorage> productInStorageMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, ProductType> productTypesMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, Integer> cookerIdIndexMap = new ConcurrentHashMap<>();

//    public static AtomicInteger operationCounter = new AtomicInteger(0);

    public EquipmentList getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(EquipmentList equipmentList) {
        var list = equipmentList.equipment;
        // If equipment is broken (at start), then skip it
        list.removeIf(item -> item.equip_active);
        this.equipmentList = equipmentList;

        for (var item : equipmentList.equipment) {
            if (equipmentMap.contains(item.equip_type)) {
                equipmentMap.get(item.equip_type).add(item);
            } else {
                equipmentMap.put(item.equip_type, new ArrayList<>() {{
                    add(item);
                }});
            }
        }
    }

    public EquipmentTypeList getEquipmentTypesList() {
        return equipmentTypeList;
    }

    public void setEquipmentTypesList(EquipmentTypeList equipmentTypeList) {
        this.equipmentTypeList = equipmentTypeList;
        for (var item : equipmentTypeList.equipment_type) {
            equipmentTypeMap.put(item.equip_type_id, item);
        }
    }

    public DishCardList getDishCardsList() {
        return dishCardList;
    }

    public void setDishCardsList(DishCardList dishCardList) {
        this.dishCardList = dishCardList;
        for (var item : dishCardList.dish_cards) {
            dishCardsMap.put(item.card_id, item);
        }
    }

    public MenuItemList getMenuList() {
        return menuList;
    }

    public void setMenuList(MenuItemList menuList) {
        this.menuList = menuList;
        for (var item : menuList.menu_dishes) {
            menuMap.put(item.menu_dish_id, item);
        }
    }

    public OperationTypesList getOperationTypesList() {
        return operationTypesList;
    }

    public void setOperationTypesList(OperationTypesList operationTypesList) {
        this.operationTypesList = operationTypesList;
        for (var item : operationTypesList.operation_types) {
            operationTypesMap.put(item.oper_type_id, item);
        }
    }

    public OperationLogsList getOperationLogsList() {
        return operationLogsList;
    }

    public void setOperationLogsList(OperationLogsList operationLogsList) {
        this.operationLogsList = operationLogsList;
        // TODO
    }

    public OrderList getOrderList() {
        return orderList;
    }

    public void setOrderList(OrderList orderList) {
        this.orderList = orderList;
        for (var item : orderList.visitors_orders) {
            orderMap.put(item.vis_name, item);
        }
    }

    public ProductInStorageList getProductInStorageList() {
        return productInStorageList;
    }

    public void setProductInStorageList(ProductInStorageList productInStorageList) {
        this.productInStorageList = productInStorageList;
        for (var item : productInStorageList.products) {
            // TYPE !!!! NOT ID
            if (productInStorageMap.contains(item.prod_item_type)) {
                var product = productInStorageMap.get(item.prod_item_type);
                product.prod_item_quantity += item.prod_item_quantity;
                productInStorageMap.put(item.prod_item_type, product);
            } else {
                productInStorageMap.put(item.prod_item_type, item);
            }
        }
    }

    public ProductTypesList getProductTypesList() {
        return productTypesList;
    }

    public void setProductTypesList(ProductTypesList productTypesList) {
        this.productTypesList = productTypesList;
        for (var item : productTypesList.product_types) {
            productTypesMap.put(item.prod_type_id, item);
        }
    }

    public CookerList getCookersList() {
        return cookerList;
    }

    public void setCookersList(CookerList cookerList) {
        this.cookerList = cookerList;
        int index = 0;
        for (var item : cookerList.cookers) {
            cookerMap.put(item.cook_id, item);
            cookerIdIndexMap.put(item.cook_id, index);
            index++;
        }
    }

    @Override
    public String toString() {
        return "DataBase{" + "\n\tcookerList=" + cookerList + ", \n\tequipmentList=" + equipmentList + ", \n\tequipmentTypeList=" + equipmentTypeList + ", \n\tdishCardsList=" + dishCardList + ", \n\tmenuList=" + menuList + ", \n\toperationTypesList=" + operationTypesList + ", \n\toperationLogsList=" + operationLogsList + ", \n\torderList=" + orderList + ", \n\tproductOnStockList=" + productInStorageList + ", \n\tproductTypesList=" + productTypesList + '}';
    }
}
