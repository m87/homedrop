package org.homedrop.core.handlers;

import org.apache.commons.io.FileUtils;
import org.homedrop.*;
import org.homedrop.core.Default;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.core.utils.json.JSONConverter;
import org.homedrop.manager.FilesManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileJSONHandler extends CommandHandler{
    /**
     * @param request Request, which invoked creation
     */
    public FileJSONHandler(Request request) {
        super(request);
    }


    /**
     * request.getCommand().getArgs()[0] - relative path to json in .HD_hd_HD/.HD_meta_HD
     * @param request request has to represent same command
     * @return
     * @throws HandlerException
     */
    @Override
    public Result handle(Request request) throws HandlerException {
        String pathJson = DBHelper.formatPath(request.getCommand().getArgs()[0]);

        FilesManager fm = FilesManager.getInstance();

        try {
            Path p = Paths.get(Default.META_TMP, pathJson);
            pathJson = fm.getHDPath(request.getUserName(), p.toString());

            String json = FileUtils.readFileToString(new File(pathJson));
            MetaPackage metaPackage = JSONConverter.toPackage(json);

            FilesManager.getInstance().prepare(metaPackage, request.getUserName(), request.getSpecialKey());



        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, e.getMessage());
            return new Result(Result.ERROR,"error");
        } catch (IOException e) {
            Log.d(LogTag.DEV, "URI error [HD_FILE:JSON]");
            return new Result(Result.ERROR,"error");
        }

        return new Result(Result.OK, "ok");
    }
}
