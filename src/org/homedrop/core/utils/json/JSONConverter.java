package org.homedrop.core.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import org.homedrop.meta.MetaFile;
import org.homedrop.meta.MetaPackage;

import java.lang.reflect.Type;
import java.util.List;

public class JSONConverter {
     public static String files(List<MetaFile> files){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
         Type listType = new TypeToken<List<MetaFile>>(){}.getType();
        return gson.toJson(files,listType);
    }

    public static List files(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(new JsonPrimitive(json), List.class);
    }

    public static MetaPackage toPackage(String json){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(MetaPackage.class, new MetaPackageSerializer());
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        return gson.fromJson(json, MetaPackage.class);


    }



}
