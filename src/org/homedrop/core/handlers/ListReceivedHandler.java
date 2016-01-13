package org.homedrop.core.handlers;

import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.manager.FilesManager;

public class ListReceivedHandler extends CommandHandler {
    public ListReceivedHandler(Request request){
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException{
        try {
            if(FilesManager.getInstance().removeList(request.getUserName(),request.getCommand().getArgs()[0])){
                return new Result(Result.OK, "ok");
            }else{
                return new Result(Result.ERROR, "error");

            }
        } catch (ItemNotFoundException e) {
            return new Result(Result.ERROR, "error");
        }
    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
