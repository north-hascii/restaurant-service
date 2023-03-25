package service.models.equipment;

import java.util.ArrayList;

/**
 * List of types of equipment in the kitchen
 */
public class EquipmentTypeList {
    public ArrayList<EquipmentType> equipment_type;

    @Override
    public String toString() {
        return "EquipmentTypeList{" +
                "equipment_type=" + equipment_type +
                '}';
    }
}
