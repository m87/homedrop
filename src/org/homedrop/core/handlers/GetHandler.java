package org.homedrop.core.handlers;

import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.exceptions.HandlerException;

public class GetHandler extends CommandHandler{
    public GetHandler(HomeDrop system) {
        super(system);
    }

    @Override
    public Result handle(String[] args, Type stage) throws HandlerException{
        return super.handle(args, stage);
    }
}
