package com.example.tienthanh.myapplication;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class SongListFragment extends Fragment implements ListSongAdapter.Listener{

    private String songUrl;
    private ArrayList<Song> songList;
    private ListSongAdapter adapter;
    private String CID;
    private ListView songListView;

    public SongListFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View v = inflater.inflate(R.layout.fragment_song_list, container, false);
        TextView categoryName = v.findViewById(R.id.category_name);
        if (bundle != null) {
            categoryName.setText(bundle.getString("CATEGORY_NAME"));
            CID = bundle.getString("CATEGORY_CID");
            songUrl = MainActivity.SONG_URL + bundle.get("CATEGORY_CID");
            songListView = v.findViewById(R.id.song_list);
            new GetSongList().execute();
        }

        return v;
    }

    @Override
    public void playSong(int position) {

        MainActivity mainActivity = (MainActivity)getActivity();
        Intent intent = new Intent(getActivity(), SongActivity.class);
        StorageUtil storageUtil = new StorageUtil(getContext());
        storageUtil.storeCID(CID);
        storageUtil.storeAudioIndex(position);
        mainActivity.playAudio(position);
        startActivity(intent);
    }

    private class GetSongList extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            songList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(songUrl);


            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray songs = jsonObj.getJSONObject("content").getJSONArray("data");

                    for (int i = 0; i < songs.length(); i++) {
                        JSONObject s = songs.getJSONObject(i);

                        String AID = s.getString("AID");
                        String name = s.getString("audioName");
                        String thumbnailUrl = s.getString("audioThumbnail");
                        String audioLink = s.getString("audioLink");
                        String author = s.getString("audioAuthor");
                        String CID = s.getString("audioCategory");
                        boolean active = s.getBoolean("isActive");
                       // Drawable thumbnail = HttpHandler.drawable_from_url(MainActivity.SERVER_URL + thumbnailUrl, getContext());
                        Song song = new Song(AID, name, thumbnailUrl, audioLink, author, CID, active);

                        songList.add(song);

                    }

                } catch (JSONException e) {

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new ListSongAdapter(getContext(), 0, songList);
            adapter.setListener(SongListFragment.this);
            songListView.setAdapter(adapter);
          new StorageUtil(getContext()).storeAudio(songList, CID);

        }
    }





}
