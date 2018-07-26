package com.example.tienthanh.myapplication;

import android.graphics.drawable.Drawable;

public class Song {
    private String AID;
    private String audioName;
    private String audioThumbnail;
    private String audioLink;
    private String audioAuthor;
    private String CID;
    private boolean isActive;

    public Song(String AID, String audioName, String audioThumbnail, String audioLink, String audioAuthor, String CID, boolean isActive) {
        this.AID = AID;
        this.audioName = audioName;
        this.audioThumbnail = audioThumbnail;
        this.audioLink = audioLink;
        this.audioAuthor = audioAuthor;
        this.CID = CID;
        this.isActive = isActive;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getAudioThumbnail() {
        return audioThumbnail;
    }

    public void setAudioThumbnail(String audioThumbnail) {
        this.audioThumbnail = audioThumbnail;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getAudioAuthor() {
        return audioAuthor;
    }

    public void setAudioAuthor(String audioAuthor) {
        this.audioAuthor = audioAuthor;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}