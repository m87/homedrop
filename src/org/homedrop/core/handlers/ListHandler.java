package org.homedrop.core.handlers;

import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.JSON;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.manager.FilesManager;

public class ListHandler extends CommandHandler{
    public ListHandler(HomeDrop system, Request request){
        super(system, request);
    }
    @Override
    public Result handle(Request request) throws HandlerException{
        super.handle(request);
        //JSON.files(FilesManager.getInstance().list(args[0]));
        //send file path
        return null;
    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
