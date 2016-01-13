package org.homedrop.core.utils;


import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.homedrop.core.Default;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * log4j wrapper
 * @see LogTag
 */
public class Log {
    private static Logger log = Logger.getLogger(Log.class.getName());
    private static FileAppender file;

    static{
        try {
            file = new FileAppender(new SimpleLayout(),"/tmp/homedrop/log");
            file.setAppend(false);
            log.addAppender(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        //String tmp = lvl+tag+": "+content;
       // try {
            //FileWriter file = new FileWriter(path);
            //file.append(lvl+tag+": " +content + '\n');
       // } catch (IOException e) {
        //    e.printStackTrace();
       // }
    }

    /**
     * Debug log level. For critical errors only!
     * @param tag Log tag
     * @param content message
     * @param file custom log file
     * @see LogTag
     */
    public static void d(String tag, String content, String file){
        String tmp = tag+": "+content;
        log.debug(tmp);
        //log("[DEBUG]",tag,content,file);
    }
    /**
     * Debug log level. For critical errors only! [Default location]
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void d(String tag, String content){
        String tmp = tag+": "+content;
        log.debug(tmp);
        //log("[DEBUG]",tag,content,Default.LOGFILE);
    }



    /**
     * Warning log level. For non critical errors i.e. Unsuccessful user creation.
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void w(String tag, String content, String file){
        String tmp = tag+": "+content;
        log.warn(tmp);
        //log("[WARNING]",tag,content,file);
    }
    /**
     * Warning log level. For non critical errors i.e. Unsuccessful user creation. [Default location]
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void w(String tag, String content){
        String tmp = tag+": "+content;
        log.warn(tmp);
        //log("[WARNING]",tag,content,Default.LOGFILE);

    }


     /**
     * Info log level. For additional information and easier debugging.
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void i(String tag, String content, String file){
        String tmp = tag+": "+content;
        log.info(tmp);
        //log("[INFO]",tag,content,file);

    }
    /**
     * Info log level. For additional information and easier debugging. [Default location]
     * @param tag Log tag
     * @param content message
     * @see LogTag
     */
    public static void i(String tag, String content){
        String tmp = tag+": "+content;
        log.info(tmp);
        //log("[INFO]",tag,content,Default.LOGFILE);

    }


}
