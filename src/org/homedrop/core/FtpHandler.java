package org.homedrop.core;

import org.homedrop.Command;
import org.homedrop.Result;

public interface FtpHandler {
    Result beforeCommand(Command command);
    Result afterCommand(Command command);
    Result onConnect();
    Result onDisconnect();
}
