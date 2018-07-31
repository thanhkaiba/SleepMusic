package com.example.tienthanh.myapplication;

import android.graphics.drawable.Drawable;

public class Category {

    private String CID;
    private String categoryName;
    private String categoryThumbnail;
    private String categoryKey;
    private int countItem;
    private boolean isActive;


    public Category() {

    }

    public Category(String CID, String categoryName, String categoryThumbnail, String categoryKey, int countItem, boolean isActive) {
        this.CID = CID;
        this.categoryName = categoryName;
        this.categoryThumbnail = categoryThumbnail;
        this.categoryKey = categoryKey;
        this.countItem = countItem;
        this.isActive = isActive;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryThumbnail() {
        return categoryThumbnail;
    }

    public void setCategoryThumbnail(String categoryThumbnail) {
        this.categoryThumbnail = categoryThumbnail;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public int getCountItem() {
        return countItem;
    }

    public void setCountItem(int countItem) {
        this.countItem = countItem;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}