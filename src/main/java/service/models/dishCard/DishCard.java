package service.models.dishCard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.models.operation.Operation;
import service.models.order.Order;
import service.models.order.OrderList;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Dish/Drink from the menu
 */
public class DishCard {
    public int card_id;
    public String dish_name;
    public String card_descr;
    public double card_time;
    public int equip_type;
    public ArrayList<Operation> operations;

    @Override
    public String toString() {
        return "DishCard{" +
                "  \n\tcard_id=" + card_id +
                ", \n\tdish_name='" + dish_name + '\'' +
                ", \n\tcard_descr='" + card_descr + '\'' +
                ", \n\tcard_time=" + card_time +
                ", \n\tequip_type=" + equip_type +
                ", \n\toperations=" + operations +
                '}';
    }

    public static DishCard parse(String data) {
        DishCard obj = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<DishCard> typeReference = new TypeReference<>() {
            };
            obj = mapper.readValue(data, typeReference);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return obj;
    }
}
