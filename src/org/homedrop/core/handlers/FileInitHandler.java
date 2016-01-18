package org.homedrop.core.handlers;

import org.homedrop.CommandHandler;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.Default;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.manager.FilesManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileInitHandler extends CommandHandler {
    /**
     * @param request Request, which invoked creation
     */
    public FileInitHandler(Request request) {
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException {

        java.io.File file = null;
        try {
            //path to session folder
            Path p = Paths.get(Default.META_TMP, "s"+String.valueOf(request.getSpecialKey()));
            Path pb = Paths.get(FilesManager.getInstance().getHDPath(request.getUserName(),Default.BUF_TMP),"s"+String.valueOf(request.getSpecialKey()));
            //create file representation
            file = new java.io.File(FilesManager.getInstance().getHDPath(request.getUserName(),p.toString()));
            //create dir sessionid with parents
            file.mkdirs();
            pb.toFile().mkdirs();

        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, e.getMessage() + " home");
            return new Result(Result.ERROR, "error");

        }
        //return session id
        return new Result(Result.OK, String.valueOf(request.getSpecialKey()));
    }
}
