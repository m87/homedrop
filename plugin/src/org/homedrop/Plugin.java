package org.homedrop;


import java.util.List;
/** Main plugin inteface */
public interface Plugin {
    /** Custom commands handler */
    Result handleRequest(Request request);
    /** All plugins have to introduce supported commands */
    List<Command> introduceCommands();
}
