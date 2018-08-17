package com.example.tienthanh.myapplication.fileloader.listener;

import com.example.tienthanh.myapplication.fileloader.pojo.FileResponse;
import com.example.tienthanh.myapplication.fileloader.request.FileLoadRequest;


/**
 * Created by krishna on 12/10/17.
 */

public interface FileRequestListener<T> {
    void onLoad(FileLoadRequest request, FileResponse<T> response);

    void onError(FileLoadRequest request, Throwable t);
}
