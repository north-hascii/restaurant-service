package service.models.product;

import java.util.ArrayList;

/**
 * List of products in storage
 */
public class ProductInStorageList {
    public ArrayList<ProductInStorage> products;

    @Override
    public String toString() {
        return "ProductOnStockList{" +
                "products=" + products +
                '}';
    }
}
