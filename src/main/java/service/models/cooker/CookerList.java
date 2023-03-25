package service.models.cooker;

import java.util.ArrayList;

/**
 * Array of all cooks
 */
public class CookerList {
    public ArrayList<Cooker> cookers;

    @Override
    public String toString() {
        return "CookersList{" +
                "cookers=" + cookers +
                '}';
    }
}
