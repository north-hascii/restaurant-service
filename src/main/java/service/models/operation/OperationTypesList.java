package service.models.operation;

import service.models.operation.OperationType;

import java.util.ArrayList;

/**
 * A list of all possible operations for the preparation of dishes/drinks
 */
public class OperationTypesList {
    @Override
    public String toString() {
        return "OperationTypesList{" +
                "operation_types=" + operation_types +
                '}';
    }

    public ArrayList<OperationType> operation_types;
}
