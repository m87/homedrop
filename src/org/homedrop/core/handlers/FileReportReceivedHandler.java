package org.homedrop.core.handlers;

import org.apache.commons.io.FileUtils;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.Default;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.manager.FilesManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReportReceivedHandler extends CommandHandler{
    /**
     * @param request Request, which invoked creation
     */
    public FileReportReceivedHandler(Request request) {
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException {
        try {
            Path p = Paths.get(Default.META_TMP, "s" + String.valueOf(request.getSpecialKey()));
            FileUtils.deleteQuietly(new File(FilesManager.getInstance().getHDPath(request.getUserName(), p.toString())));
            p = Paths.get(Default.BUF_TMP, "s" + String.valueOf(request.getSpecialKey()));
            FileUtils.deleteQuietly(new File(FilesManager.getInstance().getHDPath(request.getUserName(), p.toString())));
        }catch (ItemNotFoundException e){
            return new Result(Result.ERROR, "error");

        }
        return new Result(Result.OK, "ok");
    }
}
