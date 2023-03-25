package service.models.menuItem;

import java.util.ArrayList;


/**
 * Menu – contains a dynamic list of dishes and drinks, which may change throughout the day due to
 * exhaustion / receipt of some resources to the warehouse, the workload of cooks and/ or kitchen equipment.
 */
public class MenuItemList {
    public ArrayList<MenuItem> menu_dishes;

    @Override
    public String toString() {
        return "MenuItemList{" +
                "menu_dishes=" + menu_dishes +
                '}';
    }
}
