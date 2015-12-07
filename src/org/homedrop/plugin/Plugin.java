package org.homedrop.plugin;

import org.homedrop.core.Result;
import org.homedrop.core.model.Command;

import java.util.List;

public interface Plugin {
    Result handleCommand(String cmd, String[] args);
    List<Command> introduceCommands();
}
