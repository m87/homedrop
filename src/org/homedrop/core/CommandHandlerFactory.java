package org.homedrop.core;

import org.homedrop.CommandHandler;
import org.homedrop.Request;
import org.homedrop.core.handlers.*;

public class CommandHandlerFactory {

    public CommandHandler create(Request request){
        switch (request.getCommand().getName()){
            case HD.LIST: return new ListHandler(request);
            case HD.LIST_R: return new ListReceivedHandler(request);
            case HD.DEL: return new DelHandler(request);
            case HD.FILE: return new FileHandler(request);
            case HD.FILE_INIT: return new FileInitHandler(request);
            case HD.FILE_JSON: return new FileJSONHandler(request);
            case HD.FILE_R: return new FileReportReceivedHandler(request);
        }
        return new NoHandler(request);
    }

}
