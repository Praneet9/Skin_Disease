package com.example.skin.models;

public class SkinImage {
    private String message;
    private String percentage;

    public SkinImage(String message, String percentage) {
        this.message = message;
        this.percentage = percentage;
    }

    public SkinImage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
