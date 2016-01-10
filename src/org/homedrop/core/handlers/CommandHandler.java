package org.homedrop.core.handlers;

import org.homedrop.Command;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.exceptions.HandlerException;

/** Abstract class for command handlers */
public abstract class CommandHandler {
    /** Required stage */
    public enum Type{BEFORE, AFTER}
    private HomeDrop system;
    private Request request;

    /**
     *
     * @param system HomeDrop instance
     * @param request Request, which invoked creation
     */
    public CommandHandler(HomeDrop system, Request request){
        this.system = system;
        this.request =request;
    }

    /**
     *
     * @return base HomeDrop instance
     */
    public HomeDrop getSystem() {
        return system;
    }

    /**
     * Handles request
     * @param request request has to represent same command
     * @return Result
     * @throws HandlerException Different commands.
     */
    public Result handle(Request request) throws HandlerException{
        if(!request.getCommand().getName().equals(this.request.getCommand().getName())) throw new HandlerException();
        return null;
    }

    /**
     * Handles base request
     * @return result
     * @throws HandlerException
     */
    public Result handle() throws HandlerException{
        return handle(this.request);
    }

    /**
     *
     * @return base request
     */
    public Request getRequest() {
        return request;
    }
}
