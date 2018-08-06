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
