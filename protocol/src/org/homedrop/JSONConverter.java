package org.homedrop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class JSONConverter {
     public static String files(List<MetaFile> files){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(files);
    }

}
