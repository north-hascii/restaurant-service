package service.models.operation;

/**
 * Тип операции по приготовлению блюда/напитка
 */
public class OperationType {
    @Override
    public String toString() {
        return "OperationType{" +
                "oper_type_id=" + oper_type_id +
                ", oper_type_name='" + oper_type_name + '\'' +
                '}';
    }

    public int oper_type_id;
    public String oper_type_name;
}
