package org.homedrop.core.handlers;

import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.exceptions.HandlerException;

public abstract class CommandHandler {
    public enum Type{BEFORE, AFTER}
    private HomeDrop system;
    public CommandHandler(HomeDrop system){
        this.system = system;
    }

    public HomeDrop getSystem() {
        return system;
    }

    public Result handle(String[] args, Type stage) throws HandlerException{
            if(null != args){
                throw new HandlerException();
            }
            return null;
        }
}
