package org.homedrop.core.handlers;

import org.homedrop.CommandHandler;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.manager.FilesManager;

public class DelHandler extends CommandHandler {
    public DelHandler(Request request){
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException{
        try {
            FilesManager.getInstance().delete(request.getUserName(),request.getCommand().getArgs()[0]);
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, e.getMessage());
            return new Result(Result.ERROR, "error");
        }

        return new Result(Result.OK, "ok");

    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
