package org.homedrop.core;

import org.homedrop.Request;
import org.homedrop.core.handlers.CommandHandler;
import org.homedrop.core.handlers.ListHandler;
import org.homedrop.core.handlers.ListReceivedHandler;
import org.homedrop.core.utils.exceptions.UnsupportedCommandException;

public class CommandHandlerFactory {

    public CommandHandler create(HomeDrop sys, Request request) throws UnsupportedCommandException{
        switch (request.getCommand().getName()){
            case HD.LIST: return new ListHandler(sys, request);
            case HD.LIST_R: return new ListReceivedHandler(sys, request);
        }
        throw new UnsupportedCommandException();
    }

}
