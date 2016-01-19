package org.homedrop.core.handlers;

import org.homedrop.CommandHandler;
import org.homedrop.HandlerException;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.core.utils.exceptions.ItemWithValueAlreadyExistsException;
import org.homedrop.manager.FilesManager;

import java.io.IOException;

public class MoveHandler extends CommandHandler{
    /**
     * @param request Request, which invoked creation
     */
    public MoveHandler(Request request) {
        super(request);
    }

    @Override
    public Result handle(Request request) throws HandlerException {
        String[] paths = request.getCommand().getArgs()[0].split("#");

        try {
            FilesManager.getInstance().move(request.getUserName(), paths[0], paths[1]);
        } catch (ItemNotFoundException e) {
            return new Result(Result.ERROR, "error");
        } catch (ItemWithValueAlreadyExistsException e) {
            return new Result(Result.ERROR, "exists");
        }catch (IOException e){
            return new Result(Result.ERROR, "io");
        }

        return new Result(Result.OK, "ok");
    }
}
