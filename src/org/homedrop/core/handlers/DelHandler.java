package org.homedrop.core.handlers;

import org.homedrop.ReportElement;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.utils.ReportHelper;
import org.homedrop.core.utils.exceptions.HandlerException;

import java.util.ArrayList;
import java.util.List;

public class DelHandler extends CommandHandler{
    public DelHandler(Request request){
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException{
        return null;
        /*List<ReportElement> reportList = new ArrayList<>();



        ReportHelper.create(reportList, "");
        return new Result(Result.OK, "");*/

    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
