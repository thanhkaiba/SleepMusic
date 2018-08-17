package com.example.tienthanh.myapplication.fileloader.network;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.example.tienthanh.myapplication.Activity.LoadingActivity;
import com.example.tienthanh.myapplication.Activity.MainActivity;
import com.example.tienthanh.myapplication.BuildConfig;
import com.example.tienthanh.myapplication.Model.HttpHandler;
import com.example.tienthanh.myapplication.fileloader.utility.AndroidFileManager;
import com.example.tienthanh.myapplication.fileloader.utility.Utils;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by krishna on 12/10/17.
 */

public class FileDownloader {
    private String uri;
    private String dirName;
    private int dirType;
    private static OkHttpClient httpClient;
    private Context context;

    public FileDownloader(Context context, String uri, String dirName, int dirType) {
        this.context = context.getApplicationContext();
        this.uri = uri;
        this.dirName = dirName;
        this.dirType = dirType;
        initHttpClient();
    }

    private void initHttpClient() {
        if (httpClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            if (BuildConfig.DEBUG)
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            else
                interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build();
        }
    }

    @WorkerThread
    public File download(boolean autoRefresh) throws Exception {
        Request.Builder requestBuilder = new Request.Builder().url(uri);

        //if auto-refresh is enabled then add header "If-Modified-Since" to the request and send last modified time of local file
        File downloadFilePath = AndroidFileManager.getFileForRequest(context, uri, dirName, dirType);
        if (autoRefresh) {
            String lastModifiedTime = Utils.getLastModifiedTime(downloadFilePath.lastModified());
            if (lastModifiedTime != null) {
                requestBuilder.addHeader("If-Modified-Since", lastModifiedTime);


            }
        }

     /*   SimpleDateFormat dateFormatGmt = new SimpleDateFormat("ddMMyyyy");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String PID = "abc";
        String GUID = "com.lf.sleepsound";
        String[] item = uri.split("http://192.168.1.182:3002/apimb/v1");

        String ITEM = item[1];
        String KEY = "SLS#13122$KLF";
        String LFCODE =  GUID + "-" + PID  + "-" + dateFormatGmt.format(new Date()) + "-" + KEY + "-" + ITEM;

        requestBuilder.addHeader("guid", GUID);
        requestBuilder.addHeader("pid", PID);
        requestBuilder.addHeader("lfcode", HttpHandler.md5(LFCODE));*/

        Request request = requestBuilder.build();
        Response response = httpClient.newCall(request).execute();

        //if file on server is not modified, return null.
        if (autoRefresh && response.code() == 304) {
            return null;
        }

        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Failed to download file: " + response);
        }

        //if file already exists, delete it
        if (downloadFilePath.exists()) {
            if (downloadFilePath.delete())
                downloadFilePath = AndroidFileManager.getFileForRequest(context, uri, dirName, dirType);
        }

        //write the body to file
        BufferedSink sink = Okio.buffer(Okio.sink(downloadFilePath));
        long readBytes = sink.writeAll(response.body().source());
        sink.close();

        //check if downloaded file is not corrupt
        if (readBytes < response.body().contentLength()) {
            //delete the corrupt file
            downloadFilePath.delete();
            throw new IOException("Failed to download file: " + response);
        }

        //set server Last-Modified time to file. send this time to server on next request.
        long timeStamp = Utils.parseLastModifiedHeader(response.header("Last-Modified"));
        if (timeStamp > 0) {
            downloadFilePath.setLastModified(timeStamp);
        }

        return downloadFilePath;
    }
}
