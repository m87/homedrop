package org.homedrop.core.handlers;

import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.exceptions.HandlerException;

public class FileHandler extends CommandHandler{

    public FileHandler(Request request){
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException{
        return null;
    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}