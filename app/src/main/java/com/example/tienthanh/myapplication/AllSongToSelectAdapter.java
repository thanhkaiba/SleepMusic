package com.example.tienthanh.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AllSongToSelectAdapter extends RecyclerView.Adapter<AllSongToSelectAdapter.ViewHolder> {

    private ArrayList<Category> categoryList;
    private Context context;
    private SongToSelectAdapter.Listener listener;


    public AllSongToSelectAdapter(Context context, ArrayList<Category> categoryList, SongToSelectAdapter.Listener listener) {
        this.categoryList = categoryList;
        this.context = context;
        this.listener = listener;
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View cv = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_to_select, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Category category = categoryList.get(i);
        viewHolder.bind(category, i);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView songRecyclerView;
        private TextView categoryName;

        public ViewHolder(View v) {
            super(v);
            this.categoryName = v.findViewById(R.id.category_name);
            this.songRecyclerView = v.findViewById(R.id.song_list);
        }

        public void bind(Category category, final int position) {
           categoryName.setText(category.getCategoryName());
           ArrayList<Song> songList = AllSongFragment.songMap.get(category.getCID());
            if (songList != null) {

                SongToSelectAdapter adapter = new SongToSelectAdapter(songList, (Activity)context );
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                SimpleDividerItemDecoration simpleDividerItemDecoration = new SimpleDividerItemDecoration(context);
                songRecyclerView.addItemDecoration(simpleDividerItemDecoration);
                songRecyclerView.setLayoutManager(layoutManager);
                adapter.setListener(listener);
                songRecyclerView.setAdapter(adapter);
            }
        }



    }


}
