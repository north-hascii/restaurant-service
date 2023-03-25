package service.models.product;

import java.util.ArrayList;

/**
 * List of product types
 */
public class ProductTypesList {
    public ArrayList<ProductType> product_types;

    @Override
    public String toString() {
        return "ProductTypesList{" +
                "product_types=" + product_types +
                '}';
    }
}
