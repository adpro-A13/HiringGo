package id.ac.ui.cs.advprog.hiringgo.log.command;

import id.ac.ui.cs.advprog.hiringgo.log.model.Log;

public class LogCommandInvoker {
    private LogCommand command;

    public void setCommand(LogCommand command) {
        this.command = command;
    }

    public Log run() {
        return command.execute();
    }
}
