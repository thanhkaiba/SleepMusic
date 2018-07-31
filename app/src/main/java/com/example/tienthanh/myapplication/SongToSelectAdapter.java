package com.example.tienthanh.myapplication;


import android.app.Activity;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongToSelectAdapter extends RecyclerView.Adapter<SongToSelectAdapter.ViewHolder>{

    private ArrayList<Song> songList;
    private Activity activity;


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Listener listener;



    public interface Listener {
        void playSong(String audioLink);
        void stopSong(String audioLink);
    }

    public SongToSelectAdapter(ArrayList<Song> songList, Activity activity) {
        this.songList = songList;
        this.activity = activity;


    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View cv = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_image_item, parent, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        LinearLayout.LayoutParams layoutParams;
        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
           layoutParams = new LinearLayout.LayoutParams(displayMetrics.widthPixels/12, (int)(displayMetrics.heightPixels/6.75));
        } else {
            layoutParams = new LinearLayout.LayoutParams((int)(displayMetrics.widthPixels/6.75), displayMetrics.heightPixels/12);
        }
        cv.setLayoutParams(layoutParams);
        return new SongToSelectAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Song song = songList.get(i);
        viewHolder.bind(song, i);

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ToggleButton songIcon;
        private TextView songName;
        private View v;

        ViewHolder(View v) {
            super(v);
            this.v = v;
            songName = v.findViewById(R.id.song_name);
            this.songIcon = v.findViewById(R.id.song_image);

        }

         void bind(final Song song, final int position) {
           // Picasso.get().load(MainActivity.SERVER_URL + song.getAudioThumbnail()).into(songIcon);

           songName.setText(song.getAudioName().substring(0, 1).toUpperCase() + song.getAudioName().substring(1).toLowerCase());
            songIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (songIcon.isChecked()) {
                            songName.setTextColor(Color.CYAN);
                            listener.playSong(MainActivity.SERVER_URL + song.getAudioLink());
                            MainActivity.playingAudio.add(song);

                    } else {
                        songName.setTextColor(Color.BLACK);
                        listener.stopSong(MainActivity.SERVER_URL + song.getAudioLink());
                        MainActivity.playingAudio.remove(song);
                        if (MainActivity.playingAudio.size() == 0) {
                            MainActivity act = (MainActivity) activity;
                            act.stopPlayBack();
                        }
                    }

                }
            });

        }


    }


}
