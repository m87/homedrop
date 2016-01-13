package org.homedrop.core.utils;

import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.manager.DBManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DBHelper {
    public static String mapUserPathAsString(String userName, Path path) throws ItemNotFoundException{
        return formatPath(mapUserPath(userName, path).toString());
    }
    public static Path mapUserPath(String userName, Path path) throws ItemNotFoundException{
        String home = DBManager.getInstance().getDb().getUserByName(userName).getHome();
        return Paths.get(home, path.toString());
    }
    public static String mapUserPathAsString(String userName, String path) throws ItemNotFoundException{
        return formatPath(mapUserPath(userName, path).toString());
    }
    public static Path mapUserPath(String userName, String path) throws ItemNotFoundException{
        String home = DBManager.getInstance().getDb().getUserByName(userName).getHome();
        return Paths.get(home, path);
    }


    public static String formatPath(String path){
        String tmp = path;
        if(path.endsWith("/")){
            tmp = path.substring(0, path.length()-1);
        }
        if(path.endsWith("/.")){
            tmp = path.substring(0, path.length()-2);
        }
        return tmp;
    }

}
