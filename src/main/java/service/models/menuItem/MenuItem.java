package service.models.menuItem;

/**
 *
 * Элемент меню: блюдо/напиток
 */
public class MenuItem {
    public int menu_dish_id;
    public int menu_dish_card;
    public int menu_dish_price;
    public boolean menu_dish_active;

    @Override
    public String toString() {
        return "MenuItem{" +
                "\n\tmenu_dish_id=" + menu_dish_id +
                ", \n\tmenu_dish_card=" + menu_dish_card +
                ", \n\tmenu_dish_price=" + menu_dish_price +
                ", \n\tmenu_dish_active=" + menu_dish_active +
                '}';
    }
}
