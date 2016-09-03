package com.inderdhir.gifmaster.core;


import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.inderdhir.gifmaster.model.GifItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GifDeserializer implements JsonDeserializer<List<GifItem>> {

    private static final String DATA_KEY = "data";
    private static final String IMAGES_KEY = "images";
    private static final String FIXED_HEIGHT_KEY = "fixed_height";
    private static final String URL_KEY = "url";

    @Override
    public List<GifItem> deserialize(final JsonElement json,
                                     final Type typeOfT,
                                     final JsonDeserializationContext context)
            throws JsonParseException {

        List<GifItem> gifItemList = new ArrayList<>();
        JsonArray data = json.getAsJsonObject().get(DATA_KEY).getAsJsonArray();
        if (data != null) {
            for (JsonElement jsonElement : data) {
                String url = jsonElement.getAsJsonObject().get(IMAGES_KEY).getAsJsonObject().get(FIXED_HEIGHT_KEY)
                        .getAsJsonObject().get(URL_KEY).getAsString();
                if (url != null && !url.isEmpty()) {
                    gifItemList.add(new GifItem(url));
                }
            }
        }

        return gifItemList;
    }
}
