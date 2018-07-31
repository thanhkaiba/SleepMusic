package com.example.tienthanh.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.tienthanh.myapplication.Song;
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

   /* public void storePlaylistContent(String playlistName, MyPlaylist playlist) {

        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playlist);
        editor.putString(playlistName, json);
        editor.apply();
    }

    public MyPlaylist loadPlaylistContent(String playlistName) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(playlistName, null);
        Type type = new TypeToken<MyPlaylist>() {
        }.getType();
        return gson.fromJson(json, type);
    }*/

    public void storePlaylist(ArrayList<String> playlist) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playlist);
        editor.putString("list_of_playlist", json);
        editor.apply();
    }

    public void storePlayingAudio(ArrayList<Song> playlist) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playlist);
        editor.putString("playing_audio", json);
        editor.apply();
    }

    public ArrayList<Song> loadPlayingAudio() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("playing_audio", null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public ArrayList<String> loadPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("list_of_playlist", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void storeCategory(ArrayList<Category> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("categoryList", json);
        editor.apply();
    }

    public ArrayList<Category> loadCategory() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("categoryList", null);
        Type type = new TypeToken<ArrayList<Category>>() {
        }.getType();
        return gson.fromJson(json, type);
    }



    public void storeAudio(ArrayList<Song> arrayList, String CID) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(CID, json);
        editor.apply();
    }

    public void storeMixIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("mixIndex", index);
        editor.apply();
    }

    public void storeCID(String CID) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CategoryCID", CID);
        editor.apply();
    }

    public String loadCID() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getString("CategoryCID", "");
    }


    public int loadMixIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("mixIndex", -1);
    }

    public ArrayList<Song> loadAudio(String CID) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(CID, null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
