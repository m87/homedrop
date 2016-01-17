package org.homedrop.core.handlers;

import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.Default;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.manager.FilesManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileInitHandler extends CommandHandler{
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
            Path p = Paths.get(Default.MAIN_TMP, Default.BUF_TMP, request.getCommand().getArgs()[0]);
            file = new java.io.File(FilesManager.getInstance().getTmpPath(request.getUserName(),p.toString()));
            if(file.getParentFile() != null){
                file.getParentFile().mkdirs();
        }
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, e.getMessage() + " home");
            return new Result(Result.ERROR, "error");

        }

        return new Result(Result.OK, String.valueOf(request.getSpecialKey()));
    }
}
