package org.homedrop;


import java.util.List;

public interface Plugin {
    Result handleCommand(String cmd, String[] args);
    List<Command> introduceCommands();
}
