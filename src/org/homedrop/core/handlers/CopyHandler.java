package org.homedrop.core.handlers;

import org.homedrop.CommandHandler;
import org.homedrop.HandlerException;
import org.homedrop.Request;
import org.homedrop.Result;

public class CopyHandler extends CommandHandler{
    /**
     * @param request Request, which invoked creation
     */
    public CopyHandler(Request request) {
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException {
        return null;
    }
}
