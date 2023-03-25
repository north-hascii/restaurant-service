package service.models.operation;

import service.util.info.JSONParser;

import java.util.ArrayList;

/**
 * Operation to prepare a dish/drink
*/
public class Operation {
    public int oper_type;
    public double oper_time;

    @Override
    public String toString() {
//        return "\nOperation{" +
//                "\n\toper_type=" + oper_type +
//                ", \n\toper_time=" + oper_time +
//                ", \n\toper_async_point=" + oper_async_point +
//                ", \n\toper_products=" + oper_products +
//                '}';
        return JSONParser.gson.toJson(this);
    }

    public int oper_async_point;
    public ArrayList<ProductInfoInOperation> oper_products;
}
