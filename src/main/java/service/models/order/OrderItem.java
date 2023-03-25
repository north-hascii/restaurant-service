package service.models.order;

/**
 * Элемент блюдо/напиток из заказа
 */
public class OrderItem {
    public int ord_dish_id;
    public int menu_dish;

    @Override
    public String toString() {
        return "OrderItem{" +
                "ord_dish_id=" + ord_dish_id +
                ", menu_dish=" + menu_dish +
                '}';
    }
}
