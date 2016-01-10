package org.homedrop.core;

import org.homedrop.Request;
import org.homedrop.core.handlers.CommandHandler;
import org.homedrop.core.utils.exceptions.UnsupportedCommandException;

public class CommandHandlerFactory {

    public CommandHandler create(Request request) throws UnsupportedCommandException{
        switch (request.getCommand().getName()){

        }
        throw new UnsupportedCommandException();
    }

}
