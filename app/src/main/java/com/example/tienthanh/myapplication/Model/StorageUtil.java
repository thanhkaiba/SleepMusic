package com.example.tienthanh.myapplication.Model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StorageUtil {

    private final String STORAGE = "com.example.tienthanh.mythirdmusicapp.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    public void storeAudioLink(String audioLink) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("AudioLink", audioLink);
        editor.apply();
    }

    public String loadAudioLink() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getString("AudioLink", "");
    }



    public StorageUtil(Context context) {
        this.context = context;
    }

    public void storeCategoryList(ArrayList<Category> categoryList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(categoryList);
        editor.putString("list_of_category", json);
        editor.apply();
    }

    public void storeAudioList(String categoryCID, ArrayList<Song> audioList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(audioList);
        editor.putString("list_song_of_" + categoryCID, json);
        editor.apply();
    }

    public ArrayList<Category> loadListOfCategory() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("list_of_category", null);
        Type type = new TypeToken<ArrayList<Category>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<Song> loadAllSongList() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("all_song_list", null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void storeAllSongList(ArrayList<Song> allSongList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allSongList);
        editor.putString("all_song_list", json);
        editor.apply();
    }

    public void storeDataStatus(boolean status) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("download_status", status);
        editor.apply();
    }

    public boolean loadDataStatus() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getBoolean("download_status", false );
    }

    public ArrayList<Song> loadListOfAudio(String CID) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("list_song_of_" + CID, null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }




    public void storeListOfPlaylist(ArrayList<MyPlaylist> playlist) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playlist);
        editor.putString("list_of_playlist", json);
        editor.apply();
    }

    public ArrayList<MyPlaylist> loadListOfPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("list_of_playlist", null);
        Type type = new TypeToken<ArrayList<MyPlaylist>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
