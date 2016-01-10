package org.homedrop.core.handlers;

import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.exceptions.HandlerException;

public class ListReceivedHandler extends CommandHandler {
    public ListReceivedHandler(HomeDrop system, Request request){
        super(system, request);
    }

    @Override
    public Result handle(Request request) throws HandlerException{
        return super.handle(request);
    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}