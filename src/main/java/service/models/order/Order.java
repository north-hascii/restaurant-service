package service.models.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Order with all the information
 */
public class Order {
    public static AtomicInteger index = new AtomicInteger(0);
    public String vis_name;
    public Date vis_ord_started;
    public Date vis_ord_ended;
    public int vis_ord_total;
    public ArrayList<OrderItem> vis_ord_dishes;

    @Override
    public String toString() {
        return "Order{" +
                "vis_name='" + vis_name + '\'' +
                ", vis_ord_started=" + vis_ord_started +
                ", vis_ord_ended=" + vis_ord_ended +
                ", vis_ord_total=" + vis_ord_total +
                ", vis_ord_dishes=" + vis_ord_dishes +
                '}';
    }
}
