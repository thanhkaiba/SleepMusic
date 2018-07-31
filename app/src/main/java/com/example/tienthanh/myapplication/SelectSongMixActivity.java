package com.example.tienthanh.myapplication;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectSongMixActivity extends AppCompatActivity {

    private ArrayList<Category> categoryList;
    private RecyclerView allSongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song_mix);

      /*  categoryList = new StorageUtil(getApplicationContext()).loadCategory();
        allSongList = findViewById(R.id.all_song_list);
        AllSongToSelectAdapter adapter = new AllSongToSelectAdapter(SelectSongMixActivity.this, categoryList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SelectSongMixActivity.this);
        allSongList.setLayoutManager(layoutManager);
        allSongList.setAdapter(adapter);
*/
    }



}
