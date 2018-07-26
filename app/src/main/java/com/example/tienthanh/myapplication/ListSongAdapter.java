package com.example.tienthanh.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListSongAdapter extends ArrayAdapter<Song> {

    private Context mContext;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private ArrayList<Song> songList;
    private Listener listener;

    public interface Listener {
        void playSong(int position);
    }

    public ListSongAdapter(Context context, int resource, ArrayList<Song> songList) {
        super(context, resource);
        this.songList = songList;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.song_item_view,parent,false);

        final Song song = songList.get(position);

        TextView songName = v.findViewById(R.id.song_name);
        TextView songDuration = v.findViewById(R.id.song_duration);
        final ImageView songIcon = v.findViewById(R.id.song_icon);
        TextView songArtist = v.findViewById(R.id.song_artist);

        songName.setText(song.getAudioName());

        Picasso.get().load(MainActivity.SERVER_URL + song.getAudioThumbnail()).into(songIcon);

        songArtist.setText(song.getAudioAuthor());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playSong(position);
            }
        });

        return v;
    }




}
