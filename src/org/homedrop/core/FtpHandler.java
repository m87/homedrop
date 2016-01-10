package org.homedrop.core;

import org.homedrop.Command;
import org.homedrop.Request;
import org.homedrop.Result;

public interface FtpHandler {
    Result beforeCommand(Request command);
    Result afterCommand(Request command);
    Result onConnect();
    Result onDisconnect();
}
