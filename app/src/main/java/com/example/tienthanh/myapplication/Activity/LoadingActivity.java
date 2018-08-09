package com.example.tienthanh.myapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.tienthanh.myapplication.Model.Category;
import com.example.tienthanh.myapplication.Model.HttpHandler;
import com.example.tienthanh.myapplication.Model.Song;
import com.example.tienthanh.myapplication.Model.StorageUtil;
import com.example.tienthanh.myapplication.R;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.MultiFileDownloadListener;
import com.vlonjatg.progressactivity.ProgressConstraintLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {

    ProgressConstraintLayout progressActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        progressActivity = findViewById(R.id.progressActivity);
        progressActivity.showLoading();

        StorageUtil storageUtil = new StorageUtil(getApplicationContext());

        if (!storageUtil.loadDataStatus()) {
            loadData();
        }

        else {
            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            LoadingActivity.this.finish();
        }

    }

    private class GetAllSong extends AsyncTask<Void, Void, Void> {

        private ArrayList<Song> allSong;
        private ArrayList<Category> categoryList;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            allSong = new ArrayList<>();
            categoryList = new ArrayList<>();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(MainActivity.CATEGORY_URL);

            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray categories = jsonObj.getJSONObject("content").getJSONArray("data");

                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject c = categories.getJSONObject(i);

                        String CID = c.getString("CID");
                        String name = c.getString("categoryName");
                        String thumbnailUrl = c.getString("categoryThumbnail");
                        thumbnailUrl = "data/data/com.example.tienthanh.myapplication/files/SongImage/";

                        int countItem = c.getInt("countItem");

                        Category category = new Category(CID, name, thumbnailUrl, countItem);
                        categoryList.add(category);

                        String songUrl = MainActivity.SONG_URL + CID;
                        jsonStr = sh.makeServiceCall(songUrl);
                        jsonObj = new JSONObject(jsonStr);
                        JSONArray songs = jsonObj.getJSONObject("content").getJSONArray("data");
                        ArrayList<Song> songList = new ArrayList<>();

                        for (int j = 0; j < songs.length(); j++) {
                            JSONObject s = songs.getJSONObject(j);

                            String AID = s.getString("AID");
                            String songName = s.getString("audioName");
                            String songThumbnailUrl = s.getString("audioThumbnail");
                            String[] temp = songThumbnailUrl.split("/");
                            String songThumnail ="data/data/com.example.tienthanh.myapplication/files/AudioImage/"+ temp[temp.length-1];
                            String songMp3Link = s.getString("audioLink");
                            temp = songMp3Link.split("/");
                            String songMp3 = "data/data/com.example.tienthanh.myapplication/files/AudioMp3/"+ temp[temp.length-1];
                            String songAuthor = s.getString("audioAuthor");
                            Song song = new Song(AID, songName, songThumbnailUrl, songThumnail, songMp3Link, songMp3,  songAuthor, CID);

                            songList.add(song);
                            allSong.add(song);


                        }

                        new StorageUtil(getApplicationContext()).storeAudioList(CID, songList);
                    }
                    new StorageUtil(getApplicationContext()).storeAllSongList(allSong);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            String[] thumbnails = new String[allSong.size()];
            final String[] links =  new String[allSong.size()];

            for (int i = 0; i < allSong.size(); i++) {
                Song s = allSong.get(i);
                links[i] = MainActivity.SERVER_URL + s.getAudioMp3Link();
                thumbnails[i] = MainActivity.SERVER_URL + s.getAudioThumnailLink();

            }

            FileLoader.multiFileDownload(getApplicationContext())
                    .fromDirectory("AudioImage", FileLoader.DIR_INTERNAL)
                    .progressListener(new MultiFileDownloadListener() {
                        @Override
                        public void onProgress(File downloadedFile, int progress, int totalFiles) {
                            if (progress == totalFiles) {
                                FileLoader.multiFileDownload(getApplicationContext())
                                        .fromDirectory("AudioMp3", FileLoader.DIR_INTERNAL)
                                        .progressListener(new MultiFileDownloadListener() {
                                            @Override
                                            public void onProgress(File downloadedFile, int progress, int totalFiles) {
                                                if (progress == totalFiles) {
                                                   new StorageUtil(getApplicationContext()).storeDataStatus(true);
                                                   Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                                                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                   startActivity(intent);
                                                   LoadingActivity.this.finish();
                                                }
                                            }

                                            @Override
                                            public void onError(Exception e, int progress) {
                                                super.onError(e, progress);
                                            }
                                        }).loadMultiple(links);

                            }
                        }

                        @Override
                        public void onError(Exception e, int progress) {
                            super.onError(e, progress);
                        }
                    }).loadMultiple(thumbnails);




            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new StorageUtil(getApplicationContext()).storeCategoryList(categoryList);

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void loadData() {
        if (isNetworkConnected()) {
            new GetAllSong().execute();
        } else {
            progressActivity.showError(R.drawable.no_internet, "No Connection",
                    "We could not establish a connection with our servers. Try again when you are connected to the internet.",
                    "Try Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadData();

                        }
                    });
        }
    }
}
