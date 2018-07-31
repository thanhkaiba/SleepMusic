package com.example.tienthanh.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SongActivity extends AppCompatActivity {

    private String CID;
    private int index;
    private ArrayList<Song> songList;
    private Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        StorageUtil storageUtil = new StorageUtil(getApplicationContext());
        CID = storageUtil.loadCID();
        index = storageUtil.loadAudioIndex();
        songList = storageUtil.loadAudio(CID);



        if (index != -1 && index < songList.size()) {
            song = songList.get(index);
            initUI();
        }

    }

    private void initUI() {
        TextView songName = findViewById(R.id.song_name);
        songName.setText(song.getAudioName());
        ImageView songImage = findViewById(R.id.song_image);
        Picasso.get().load(MainActivity.SERVER_URL + song.getAudioThumbnail()).into(songImage);
        ImageView mix = findViewById(R.id.song_mix);


        mix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MixDialog mixDialog = new MixDialog(SongActivity.this);
                mixDialog.show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
