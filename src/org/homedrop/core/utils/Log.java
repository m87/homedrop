package org.homedrop.core.utils;


import org.homedrop.core.Default;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** Main log class */
public class Log {
    /** Runtime messages */
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

    /** Debug message in custom log file */
    public static void d(String tag, String content, String file){
        log("[DEBUG]",tag,content,file);
    }
    /** Debug message in default log file */
    public static void d(String tag, String content){
        log("[DEBUG]",tag,content,Default.LOGFILE);
    }



    /** Warning message in custom log file */
    public static void w(String tag, String content, String file){

        log("[WARNING]",tag,content,file);
    }
    /** Warning message in default log file */
    public static void w(String tag, String content){
        log("[WARNING]",tag,content,Default.LOGFILE);

    }


    /** Info message in custom log file */
    public static void i(String tag, String content, String file){
        log("[INFO]",tag,content,file);

    }
    /** Info message in default log file */
    public static void i(String tag, String content){
        log("[INFO]",tag,content,Default.LOGFILE);

    }


}
