package id.ac.ui.cs.advprog.hiringgo.log.command;

public class LogCommandInvoker {
    private LogCommand command;

    public void setCommand(LogCommand command) {
        this.command = command;
    }

    public Object run() {
        return command.execute();
    }
}
