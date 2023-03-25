package service.models.order;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderList {
    public static AtomicInteger index = new AtomicInteger(0);
    public ArrayList<Order> visitors_orders;

    @Override
    public String toString() {
        return "OrderList{" +
                "visitors_orders=" + visitors_orders +
                '}';
    }
}
