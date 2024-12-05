package com.hmir.goodfood;

public class TipItem {
    private int imageResId; // Resource ID for the image
    private String title;
    private String description;

    // Constructor
    public TipItem(int imageResId, String title, String description) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
    }

    // Getters
    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
