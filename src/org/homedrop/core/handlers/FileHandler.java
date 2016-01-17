package org.homedrop.core.handlers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.homedrop.*;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.core.utils.json.JSONConverter;
import org.homedrop.manager.FilesManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class FileHandler extends CommandHandler{

    public FileHandler(Request request){
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException{
        //String pathJson = DBHelper.formatPath(request.getCommand().getArgs()[0])+".json."+String.valueOf(request.getSpecialKey());
        String pathJson = DBHelper.formatPath(request.getCommand().getArgs()[0]);

        FilesManager fm = FilesManager.getInstance();
        try {
            pathJson = fm.getTmpPath(request.getUserName(), pathJson);


            String json = FileUtils.readFileToString(new File(pathJson));
            MetaPackage metaPackage = JSONConverter.toPackage(json);
            List<MetaFile> files = Arrays.asList(metaPackage.files);

            for(MetaFile file : files){
                fm.addFileFromMeta(request.getUserName(), file, request.getSpecialKey());
            }


        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, e.getMessage());
            return new Result(Result.ERROR,"error");
        } catch (IOException e) {
            Log.d(LogTag.DEV, "URI error [HD_FILE:JSON]");
            return new Result(Result.ERROR,"error");
        }

        return new Result(Result.OK, "ok");
    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
