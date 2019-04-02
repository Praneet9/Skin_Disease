package com.example.skin.models;
import com.google.gson.JsonObject;

public class ResponseBody {

    private boolean status;
    private String image_path;
    private String photo_path;
    private JsonObject fields;

    public boolean getStatus() {
        return status;
    }

    public JsonObject getFields() {
        return fields;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getPhoto_path() {
        return photo_path;
    }
}
