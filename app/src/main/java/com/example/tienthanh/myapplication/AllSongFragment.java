package com.example.tienthanh.myapplication;


import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class AllSongFragment extends Fragment implements SongToSelectAdapter.Listener {

    public static ArrayList<Category> categoryList;
    public static HashMap<String, ArrayList<Song>> songMap;
    private RecyclerView allSongList;

    public AllSongFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_select_song_mix, container, false);
        allSongList = v.findViewById(R.id.all_song_list);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
       if (categoryList == null || categoryList.size() == 0) {
           new GetAllSong().execute();
       } else {
           AllSongToSelectAdapter adapter = new AllSongToSelectAdapter(getContext(), categoryList, AllSongFragment.this);
           LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
           allSongList.setLayoutManager(layoutManager);
           allSongList.setAdapter(adapter);
       }
    }

    @Override
    public void playSong(String audioLink) {
        MainActivity activity = (MainActivity) getActivity();
        activity.playAudio(audioLink);
    }

    @Override
    public void stopSong(String audioLink) {
        MainActivity.player.removeSelectedAudioFromMix(audioLink);
    }

    private class GetAllSong extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            categoryList = new ArrayList<>();
            songMap = new HashMap<>();
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
                        String thumbnail = c.getString("categoryThumbnail");
                        int countItem = c.getInt("countItem");
                        String key = c.getString("categoryKey");
                        boolean active = c.getBoolean("isActive");
                        Category category = new Category(CID, name, thumbnail, key, countItem, active);
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
                            String songAudioLink = s.getString("audioLink");
                            String songAuthor = s.getString("audioAuthor");
                            boolean songActive = s.getBoolean("isActive");
                            Song song = new Song(AID, songName, songThumbnailUrl, songAudioLink, songAuthor, CID, songActive);
                            songList.add(song);


                        }
                        songMap.put(CID, songList);
                    }

                } catch (JSONException e) {

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AllSongToSelectAdapter adapter = new AllSongToSelectAdapter(getContext(), categoryList, AllSongFragment.this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            allSongList.setLayoutManager(layoutManager);
            allSongList.setAdapter(adapter);
        }
    }



}
