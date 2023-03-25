package service.models.process;

import java.util.ArrayList;

/**
 * A list of all the processes of creating dishes/drinks from the menu
 */
public class ProcessOperationList {
    public ArrayList<ProcessLog> process_log;

    @Override
    public String toString() {
        return "CookingProcessOperationList{" +
                "process_log=" + process_log +
                '}';
    }
}
