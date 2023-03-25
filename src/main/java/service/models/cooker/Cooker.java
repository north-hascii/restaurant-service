package service.models.cooker;

/**
 * Cook is a person involved in operations for the preparation of dishes and drinks
 * (characterized by a certain hourly wage, his downtime is also paid, therefore undesirable),
 * interacts with the managing agent through a touch terminal in the kitchen.
 */
public class Cooker {
    public int cook_id;
    public String cook_name;
    public boolean cook_active;

    @Override
    public String toString() {
        return "cooker=[{" +
                "cook_id=" + cook_id +
                ", cook_name='" + cook_name + '\'' +
                ", cook_active=" + cook_active +
                "}]";
    }
}
