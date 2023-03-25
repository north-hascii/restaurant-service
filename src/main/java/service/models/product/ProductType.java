package service.models.product;

/**
 * Тип продукта
 */
public class ProductType {
    public int prod_type_id;
    public String prod_type_name;
    public boolean prod_is_food;

    @Override
    public String toString() {
        return "ProductType{" +
                "prod_type_id=" + prod_type_id +
                ", prod_type_name='" + prod_type_name + '\'' +
                ", prod_is_food=" + prod_is_food +
                '}';
    }
}
