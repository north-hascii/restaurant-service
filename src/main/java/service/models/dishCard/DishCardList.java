package service.models.dishCard;

import java.util.ArrayList;

/**
 * List of all dishes/drinks from the menu
 */
public class DishCardList {
    public ArrayList<DishCard> dish_cards;

    public DishCardList() {

    }

    public DishCardList(ArrayList<DishCard> verifyOrder) {
        dish_cards = verifyOrder;
    }

    @Override
    public String toString() {
        return "DishCardsList{" +
                "dish_cards=" + dish_cards +
                '}';
    }
}
