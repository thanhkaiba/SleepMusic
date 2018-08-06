package com.example.tienthanh.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tienthanh.myapplication.Model.MyPlaylist;
import com.example.tienthanh.myapplication.R;

import java.util.ArrayList;

public class MixesAdapter extends RecyclerView.Adapter<MixesAdapter.ViewHolder> {

    private ArrayList<MyPlaylist> listPlayList;
    private Context context;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Listener listener;

    public interface Listener {
        void onClick(int position);
    }

    public MixesAdapter(Context context, ArrayList<MyPlaylist> listPlayList) {
        this.listPlayList = listPlayList;
        this.context = context;
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View cv = LayoutInflater.from(parent.getContext()).inflate(R.layout.mixes_item_view, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        MyPlaylist myPlaylist = listPlayList.get(i);
        viewHolder.bind(myPlaylist, i);

    }

    @Override
    public int getItemCount() {
        return listPlayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private TextView playlistName;
        private TextView playlistSize;

        public ViewHolder(View v) {
            super(v);
            playlistName = v.findViewById(R.id.playlist_name);
            playlistSize = v.findViewById(R.id.playlist_size);
        }

        public void bind(MyPlaylist myPlaylist, final int position) {



            playlistName.setText(myPlaylist.getName());

            playlistSize.setText("" + myPlaylist.getSize() + " songs");

        }


    }
}
