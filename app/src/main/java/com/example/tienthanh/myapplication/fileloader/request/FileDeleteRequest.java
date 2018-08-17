package com.example.tienthanh.myapplication.fileloader.request;

import java.util.ArrayList;

/**
 * Created by krishna on 15/10/17.
 */

public class FileDeleteRequest {
    private ArrayList<String> fileUriList;
    private String directoryName;
    private int directoryType;

    public FileDeleteRequest(ArrayList<String> fileUriList, String directoryName, int directoryType) {
        this.fileUriList = fileUriList;
        this.directoryName = directoryName;
        this.directoryType = directoryType;
    }

    public ArrayList<String> getFileUriList() {
        return fileUriList;
    }

    public void setFileUriList(ArrayList<String> fileUriList) {
        this.fileUriList = fileUriList;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public int getDirectoryType() {
        return directoryType;
    }

    public void setDirectoryType(int directoryType) {
        this.directoryType = directoryType;
    }
}
