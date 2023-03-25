package service.agents;

import service.models.dishCard.DishCard;

public class DataFromProcess {
    Integer processID;
    DishCard dishCard;

    public DataFromProcess(Integer processID, DishCard dishCard) {
        this.processID = processID;
        this.dishCard = dishCard;
    }
}
