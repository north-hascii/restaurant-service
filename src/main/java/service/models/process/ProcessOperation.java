package service.models.process;

/**
 * The process of creating a dish/drink from the menu
 */
public class ProcessOperation {

    public int proc_oper;

    public ProcessOperation(int proc_oper) {
        this.proc_oper = proc_oper;
    }

    @Override
    public String toString() {
        return "CookingProcessOperation{" +
                "\n\tproc_oper=" + proc_oper +
                '}';
    }
}
