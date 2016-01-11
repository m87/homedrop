package org.homedrop.core.handlers;

import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.exceptions.HandlerException;

public class ListHandler extends CommandHandler{
    public ListHandler(HomeDrop system, Request request){
        super(system, request);
    }

    /**
     * Creates file with list of files in requested folder. For file request returns IS_FILE code
     * @param request request has to represent same command
     * @return result IS_FILE for files
     * @throws HandlerException
     */
    @Override
    public Result handle(Request request) throws HandlerException{
        super.handle(request);
        //String json = JSON.files(FilesManager.getInstance().list(request.getUserName(),request.getCommand().getArgs()[0]));

        String json = "AAAAAAA";
/*
        //Path path = Paths.get(FilesManager.getInstance().getHome(request.getUserName()), Default.MAIN_TMP, Default.LIST_TMP,
          //       request.getCommand().getArgs()[0], Default.LIST_NAME);
        //File file = new File(path.toString());
        File file = new File("/home/rt/test2/.hd/.lists/list");
        File file2 = new File("/home/rt/test2/.hd/.lists");

        if(file2.getParentFile() != null){
            if(!file.mkdirs()){
                Log.w(LogTag.HOMEDROP, "Couldn't create dirs for list file.");
                new Result(Result.ERROR);
            }
        }

        try {
            //FileUtils.writeStringToFile(new File(path.toString()), json);
            FileUtils.writeStringToFile(file, json);
        } catch (IOException e) {
            Log.w(LogTag.HOMEDROP, "Couldn't create list file.[IOException]");
        }
*/
        return new Result(Result.OK);
    }

    @Override
    public Result handle() throws HandlerException {
        return  handle(getRequest());
    }
}
