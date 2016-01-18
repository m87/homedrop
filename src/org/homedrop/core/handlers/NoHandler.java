package org.homedrop.core.handlers;

import org.homedrop.CommandHandler;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.HandlerException;

public class NoHandler extends CommandHandler {
    /**
     * @param request Request, which invoked creation
     */
    public NoHandler(Request request) {
        super(request);
    }

    @Override
    public Result handle() throws HandlerException {
        return new Result(Result.UNSUPPORTED, "");
    }

    @Override
    public Result handle(Request request) throws HandlerException {
        return new Result(Result.UNSUPPORTED, "");
    }
}
