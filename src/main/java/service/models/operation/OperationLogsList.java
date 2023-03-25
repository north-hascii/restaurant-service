package service.models.operation;

import service.models.operation.OperationLog;

import java.util.ArrayList;

public class OperationLogsList {
    @Override
    public String toString() {
        return "OperationLogsList{" +
                "operation_log=" + operation_log +
                '}';
    }

    public ArrayList<OperationLog> operation_log;
}
