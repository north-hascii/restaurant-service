package service.models.operation;

public class ProductInfoInOperation {
    public int prod_type;
    public double prod_quantity;
    public int prod_type_id;

    @Override
    public String toString() {
        return "\nProductInfoInOperation{" +
                "\n\t\tprod_type=" + prod_type +
                ", \n\t\tprod_quantity=" + prod_quantity +
                ", \n\t\tprod_type_id=" + prod_type_id +
                '}';
    }
}
