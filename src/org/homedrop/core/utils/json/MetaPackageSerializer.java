package org.homedrop.core.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.homedrop.MetaPackage;

import java.lang.reflect.Type;

public class MetaPackageSerializer implements JsonSerializer<MetaPackage>{
    @Override
    public JsonElement serialize(MetaPackage metaPackage, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonObject jsonObject = new JsonObject();

        final JsonElement jsonAuthros = jsonSerializationContext.serialize(metaPackage.settings);
        jsonObject.add("settings", jsonAuthros);

        final JsonElement jsonfiles = jsonSerializationContext.serialize(metaPackage.files);
        jsonObject.add("files", jsonfiles);


        return jsonObject;
    }
}
