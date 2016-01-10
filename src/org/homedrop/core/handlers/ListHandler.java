package org.homedrop.core.handlers;

import org.apache.commons.io.FileUtils;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.Default;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.JSON;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.FilesManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ListHandler extends CommandHandler{
    public ListHandler(HomeDrop system, Request request){
        super(system, request);
    }

    @Override
    public Result handle(Request request) throws HandlerException{
        super.handle(request);
        String json = JSON.files(FilesManager.getInstance().list(request.getCommand().getArgs()[0]));


        Path path = Paths.get(FilesManager.getInstance().getHome(request.getUserName()), Default.MAIN_TMP, Default.LIST_TMP,
                 request.getCommand().getArgs()[0], Default.LIST_NAME);
        File file = new File(path.toString());

        if(file.getParentFile() != null){
            if(!file.mkdirs()){
                Log.w(LogTag.HOMEDROP, "Couldn't create dirs for list file.");
                new Result(Result.ERROR);
            }
        }

        try {
            FileUtils.writeStringToFile(new File(path.toString()), json);
        } catch (IOException e) {
            Log.w(LogTag.HOMEDROP, "Couldn't create list file.[IOException]");
        }

        return new Result(Result.OK);
    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
