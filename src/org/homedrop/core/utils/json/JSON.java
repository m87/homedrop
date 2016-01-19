package org.homedrop.core.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.homedrop.meta.MetaFile;
import org.homedrop.meta.ReportElement;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Rule;
import org.homedrop.core.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class JSON {

    public static String createReport(List<ReportElement> elements){
        String out="";
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        out = gson.toJson(elements);
        return out;
    }

    public static List<ReportElement> parseReport(String report){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        List<ReportElement> out = gson.fromJson(report, List.class);
        return out;
    }

    public static String files(List<File> files){
        List<MetaFile> tmp = new ArrayList<>();
        for(File file : files){
            tmp.add(new MetaFile(file.getPath()));
        }
        return JSONConverter.files(tmp);
    }
    public static List<File> files(String files){
        return null;
    }
    public static String rules(List<Rule> rules){
        return null;
    }
    public static String tags(List<Tag> tags){
        return null;
    }
    public static List<Rule> rules(String rules){
        return null;
    }
    public static List<Tag> tags(String tags){
        return null;
    }
}
