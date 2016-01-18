package org.homedrop;


/** Abstract class for command handlers */
public abstract class CommandHandler {
    /** Required stage */
    public enum Type{BEFORE, AFTER}
    private Request request;

    /**
     *
     * @param request Request, which invoked creation
     */
    public CommandHandler(Request request){
        this.request =request;
    }

    /**
     * Handles request
     * @param request request has to represent same command
     * @return Result
     * @throws HandlerException Different commands.
     */
    public abstract Result handle(Request request) throws HandlerException;/*{
        if(!request.getCommand().getName().equals(this.request.getCommand().getName())) throw new HandlerException();
    }
*/
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
