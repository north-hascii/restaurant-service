package service.models.equipment;

import java.util.ArrayList;

/**
 * List of all available equipment in the kitchen
 */
public class EquipmentList {
    public ArrayList<Equipment> equipment;

    @Override
    public String toString() {
        return "EquipmentList{" +
                "equipment=" + equipment +
                '}';
    }
}
