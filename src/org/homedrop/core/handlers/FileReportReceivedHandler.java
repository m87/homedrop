package org.homedrop.core.handlers;

import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.utils.exceptions.HandlerException;

public class FileReportReceivedHandler extends CommandHandler{
    /**
     * @param request Request, which invoked creation
     */
    public FileReportReceivedHandler(Request request) {
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException {
        return null;
    }
}
