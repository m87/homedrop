package org.homedrop.core.handlers;

import org.homedrop.core.utils.exceptions.HandlerException;

public abstract class CommandHandler {
        public void handle(String[] args) throws HandlerException{
            if(null != args){
                throw new HandlerException();
            }
        }
}
