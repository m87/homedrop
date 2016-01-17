package org.homedrop.core.handlers;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.homedrop.ReportElement;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.ReportHelper;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DelHandler extends CommandHandler{
    public DelHandler(Request request){
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException{
        try {
            FileUtils.deleteQuietly(new File(DBHelper.mapUserPathAsString(request.getUserName(), request.getCommand().getArgs()[0])));
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, e.getMessage());
            return new Result(Result.ERROR, "");
        }

        return new Result(Result.OK, "");

    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
