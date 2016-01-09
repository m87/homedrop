package org.homedrop.core.handlers;

import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.JSON;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.manager.FilesManager;

public class ListHandler extends CommandHandler{
    public ListHandler(HomeDrop system){
        super(system);
    }
    @Override
    public Result handle(String[] args, Type stage) throws HandlerException{
        super.handle(args, stage);
        if(stage == Type.AFTER){
        JSON.files(FilesManager.getInstance().list(args[0]));
        //send file path
        }
        return null;
    }
}
