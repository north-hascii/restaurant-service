package service.models.equipment;

/**
 * Type of equipment in the kitchen
 */
public class EquipmentType {
    public int equip_type_id;
    public String equip_type_name;

    @Override
    public String toString() {
        return "EquipmentType{" +
                "equip_type_id=" + equip_type_id +
                ", equip_type_name='" + equip_type_name + '\'' +
                '}';
    }
}
