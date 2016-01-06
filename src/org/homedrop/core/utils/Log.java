package org.homedrop.core.utils;


import org.homedrop.core.Default;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * log4j wrapper
 * @see LogTag
 */
public class Log {
    /**
     * Runtime message
     *
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void p(String tag, String content){
        System.out.println(tag + ": " + content);
    }

    private static void log(String lvl, String tag, String content, String path){
        try {
            FileWriter file = new FileWriter(path);
            file.append(lvl+tag+": " +content + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Debug log level. For critical errors only!
     * @param tag Log tag
     * @param content message
     * @param file custom log file
     * @see LogTag
     */
    public static void d(String tag, String content, String file){
        log("[DEBUG]",tag,content,file);
    }
    /**
     * Debug log level. For critical errors only! [Default location]
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void d(String tag, String content){
        log("[DEBUG]",tag,content,Default.LOGFILE);
    }



    /**
     * Warning log level. For non critical errors i.e. Unsuccessful user creation.
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void w(String tag, String content, String file){

        log("[WARNING]",tag,content,file);
    }
    /**
     * Warning log level. For non critical errors i.e. Unsuccessful user creation. [Default location]
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void w(String tag, String content){
        log("[WARNING]",tag,content,Default.LOGFILE);

    }


     /**
     * Info log level. For additional information and easier debugging.
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void i(String tag, String content, String file){
        log("[INFO]",tag,content,file);

    }
    /**
     * Info log level. For additional information and easier debugging. [Default location]
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void i(String tag, String content){
        log("[INFO]",tag,content,Default.LOGFILE);

    }


}
