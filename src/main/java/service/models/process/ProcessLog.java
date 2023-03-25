package service.models.process;

import java.util.ArrayList;
import java.util.Date;

public class ProcessLog {
    public int proc_id;
    public int ord_dish;
    public Date proc_started;
    public Date proc_ended;
    public boolean proc_active;
    public ArrayList<ProcessOperation> proc_operations;

    public ProcessLog(int proc_id, int ord_dish, Date proc_started, Date proc_ended, boolean proc_active, ArrayList<ProcessOperation> proc_operations) {
        this.proc_id = proc_id;
        this.ord_dish = ord_dish;
        this.proc_started = proc_started;
        this.proc_ended = proc_ended;
        this.proc_active = proc_active;
        this.proc_operations = proc_operations;
    }

    @Override
    public String toString() {
        return "CookingProcessLog{" +
                "proc_id=" + proc_id +
                ", ord_dish=" + ord_dish +
                ", proc_started=" + proc_started +
                ", proc_ended=" + proc_ended +
                ", proc_active=" + proc_active +
                ", proc_operations=" + proc_operations +
                '}';
    }
}
