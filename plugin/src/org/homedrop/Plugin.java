package org.homedrop;


import java.util.List;
/** Main plugin inteface */
public interface Plugin {
    /** Custom commands handler */
    Result handleCommand(String cmd, String[] args);
    /** All plugins have to introduce supported commands */
    List<Command> introduceCommands();
}
