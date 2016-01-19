package org.homedrop.core.handlers;

import org.apache.commons.io.FileUtils;
import org.homedrop.CommandHandler;
import org.homedrop.HandlerException;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.core.utils.exceptions.ItemWithValueAlreadyExistsException;
import org.homedrop.manager.DBManager;
import org.homedrop.manager.FilesManager;

import java.io.File;
import java.nio.file.Paths;

public class RenameHandler extends CommandHandler{
    /**
     * @param request Request, which invoked creation
     */
    public RenameHandler(Request request) {
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException {
        String[] paths = request.getCommand().getArgs()[0].split("#");

        try {
            DBManager.getInstance().getDb().renameFile(request.getUserName(), paths[0], paths[1]);
            File fileSrc = Paths.get(FilesManager.getInstance().getHome(request.getUserName()),paths[0]).toFile();
            File fileDst = Paths.get(FilesManager.getInstance().getHome(request.getUserName()),paths[1]).toFile();
            fileSrc.renameTo(fileDst);

        } catch (ItemNotFoundException e) {
            return new Result(Result.ERROR,"error");
        } catch (ItemWithValueAlreadyExistsException e) {
            return new Result(Result.ERROR, "exists");
        }

        return new Result(Result.OK, "ok");
    }
}
