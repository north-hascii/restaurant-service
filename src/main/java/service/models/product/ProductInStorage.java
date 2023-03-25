package service.models.product;

import java.util.Date;

/**
 * Product from the warehouse
 */
public class ProductInStorage {
    public int prod_item_id;
    public int prod_item_type;
    public String prod_item_name;
    public String prod_item_company;
    public String prod_item_unit;
    public double prod_item_quantity;
    public double prod_item_cost;
    public Date prod_item_delivered;
    public Date prod_item_valid_until;

    @Override
    public String toString() {
        return "\nProductInStorage{" +
                "\n\tprod_item_id=" + prod_item_id +
                ", \n\tprod_item_type=" + prod_item_type +
                ", \n\tprod_item_name='" + prod_item_name + '\'' +
                ", \n\tprod_item_company='" + prod_item_company + '\'' +
                ", \n\tprod_item_unit='" + prod_item_unit + '\'' +
                ", \n\tprod_item_quantity=" + prod_item_quantity +
                ", \n\tprod_item_cost=" + prod_item_cost +
                ", \n\tprod_item_delivered=" + prod_item_delivered +
                ", \n\tprod_item_valid_until=" + prod_item_valid_until +
                '}';
    }
}
