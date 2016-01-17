package org.homedrop.core.handlers;

import org.apache.commons.io.FileUtils;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.Default;
import org.homedrop.core.model.File;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.core.utils.json.JSON;
import org.homedrop.manager.FilesManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ListHandler extends CommandHandler{
    public ListHandler(Request request){
        super(request);
    }

    /**
     * Creates file with list of files in requested folder. For file request returns IS_FILE code
     * @param request request has to represent same command
     * @return result IS_FILE for files
     * @throws HandlerException
     */
    @Override
    public Result handle(Request request) throws HandlerException{
       // super.handle(request);
        //TODO relative path in json, indexall
        if(!request.getCommand().getName().equals(getRequest().getCommand().getName())) throw new HandlerException();
        List<File> t = FilesManager.getInstance().list(request.getUserName(), request.getCommand().getArgs()[0]);
        String json = JSON.files(t);

        Path path = null;
        try {
            path = Paths.get(FilesManager.getInstance().getHome(request.getUserName()), Default.MAIN_TMP, Default.LIST_TMP,
                    request.getCommand().getArgs()[0], Default.LIST_NAME + String.valueOf(request.getSpecialKey()));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
        }
        java.io.File file = new java.io.File(path.toString());

        if(file.getParentFile() != null){
            file.getParentFile().mkdirs();
        }

        try {
            FileUtils.writeStringToFile(file, json);
        } catch (IOException e) {
            Log.w(LogTag.HOMEDROP, "Couldn't create list file.[IOException]");
            e.printStackTrace();
        }
        return new Result(Result.OK, DBHelper.removeHome(request.getUserName(),path.toString()));
    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
