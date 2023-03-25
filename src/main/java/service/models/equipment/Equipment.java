package service.models.equipment;

/**
 * Equipment for use in the kitchen
 */
public class Equipment {
    public int equip_type;
    public String equip_name;
    public Boolean equip_active;


    @Override
    public String toString() {
        return "Equipment{" +
                "equip_type=" + equip_type +
                ", equip_name='" + equip_name + '\'' +
                ", equip_active=" + equip_active +
                '}';
    }
}
