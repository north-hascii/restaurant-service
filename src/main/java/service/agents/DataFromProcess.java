package service.agents;

import service.models.dishCard.DishCard;

public class DataFromProcess {
    Integer processID;
    DishCard dishCard;
    String processAgentName;

    public DataFromProcess(Integer processID, DishCard dishCard, String processAgentName) {
        this.processID = processID;
        this.processAgentName = processAgentName;
        this.dishCard = dishCard;
    }
}
