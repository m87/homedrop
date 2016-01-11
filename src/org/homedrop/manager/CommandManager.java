package org.homedrop.manager;

import org.homedrop.Plugin;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.CommandHandlerFactory;
import org.homedrop.core.FtpHandler;
import org.homedrop.core.LifeCycle;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.UnsupportedCommandException;

import java.util.Map;

public class CommandManager implements LifeCycle, FtpHandler{
    private static CommandManager ourInstance = new CommandManager();
    private CommandHandlerFactory handlerFactory = new CommandHandlerFactory();

    public static CommandManager getInstance() {
        return ourInstance;
    }

    private CommandManager() {
    }



    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onExit() {

    }

    @Override
    public Result beforeCommand(Request command) {
        try {
          return  handlerFactory.create(command).handle();
        } catch (HandlerException e) {
            Log.w(LogTag.HOMEDROP, e.getMessage());
        }

        /*
        //TODO PluginGuard.exectuePlugins();
        Map<String, Plugin> map = PluginsManager.getInstance().getPlugins();
        for (String p : map.keySet()) {
            System.out.print("[" + map.get(p) + "]: ");
            map.get(p).handleRequest(command);

        }

        */
        return new Result(Result.UNSUPPORTED,"");
    }

    @Override
    public Result afterCommand(Request command) {
        return null;
    }

    @Override
    public Result onConnect() {
        return null;
    }

    @Override
    public Result onDisconnect() {
        return null;
    }
}
