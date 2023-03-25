package service.models.operation;

import java.util.Date;

public class OperationLog {
    public OperationLog(int oper_id, int oper_proc, int oper_card, Date oper_started, Date oper_ended, int oper_coocker_id, boolean oper_active) {
        this.oper_id = oper_id;
        this.oper_proc = oper_proc;
        this.oper_card = oper_card;
        this.oper_started = oper_started;
        this.oper_ended = oper_ended;
        this.oper_coocker_id = oper_coocker_id;
        this.oper_active = oper_active;
    }

    @Override
    public String toString() {
        return "OperationLog{" +
                "oper_id=" + oper_id +
                ", oper_proc=" + oper_proc +
                ", oper_card=" + oper_card +
                ", oper_started=" + oper_started +
                ", oper_ended=" + oper_ended +
                ", oper_coocker_id=" + oper_coocker_id +
                ", oper_active=" + oper_active +
                '}';
    }

    public int oper_id;
    public int oper_proc;
    public int oper_card;
    public Date oper_started;
    public Date oper_ended;
    public int oper_coocker_id;
    public boolean oper_active;
}
