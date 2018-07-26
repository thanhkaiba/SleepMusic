package com.example.tienthanh.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SongActivity extends AppCompatActivity {

    private String CID;
    private int index;
    private ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        StorageUtil storageUtil = new StorageUtil(getApplicationContext());
        CID = storageUtil.loadCID();
        index = storageUtil.loadAudioIndex();
        songList = storageUtil.loadAudio(CID);



        if (index != -1 && index < songList.size()) {
            Song song = songList.get(index);

            TextView songName = findViewById(R.id.song_name);
            songName.setText(song.getAudioName());
            ImageView songImage = findViewById(R.id.song_image);
            Picasso.get().load(MainActivity.SERVER_URL + song.getAudioThumbnail()).into(songImage);
        }

    }
}
