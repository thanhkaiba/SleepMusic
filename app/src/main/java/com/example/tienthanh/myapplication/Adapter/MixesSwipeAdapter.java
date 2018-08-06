package com.example.tienthanh.myapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tienthanh.myapplication.Model.MyPlaylist;
import com.example.tienthanh.myapplication.Model.StorageUtil;
import com.example.tienthanh.myapplication.R;
import com.tr4android.recyclerviewslideitem.SwipeAdapter;
import com.tr4android.recyclerviewslideitem.SwipeConfiguration;

import java.util.ArrayList;

public class MixesSwipeAdapter extends SwipeAdapter{

    private ArrayList<MyPlaylist> listPlayList;
    private Context context;


    private Listener listener;


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void playMixes(int position);

    }

    public MixesSwipeAdapter(Context context, ArrayList<MyPlaylist> listPlayList) {
        this.context = context;
        this.listPlayList = listPlayList;
    }

    @Override
    public MixesSwipeAdapter.ViewHolder onCreateSwipeViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mixes_item_view, parent, true);
        return new ViewHolder(v);
    }


    @Override
    public void onBindSwipeViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        MyPlaylist myPlaylist = listPlayList.get(i);
        MixesSwipeAdapter.ViewHolder view = (MixesSwipeAdapter.ViewHolder) viewHolder;
        view.bind(myPlaylist, i);
    }

    @Override
    public int getItemCount() {
        return listPlayList.size();
    }

    @Override
    public SwipeConfiguration onCreateSwipeConfiguration(Context context, int i) {
        return new SwipeConfiguration.Builder(context)
                .setBackgroundColorResource(R.color.album_title)
                .setDrawableResource(R.drawable.ic_delete_white_24dp)
                .setUndoable(true)
                .setUndoButtonText("Undo")
                .setUndoDescription("DELETED")
                .setDescriptionTextColor(Color.WHITE)
                .build();
    }

    @Override
    public void onSwipe(int position, int direction) {
        listPlayList.remove(position);
        new StorageUtil(context).storeListOfPlaylist(listPlayList);

        notifyItemRemoved(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton btnPlay;
        private TextView playlistName;
        private TextView playlistSize;

        public ViewHolder(View v) {
            super(v);
            btnPlay = v.findViewById(R.id.btn_play);
            playlistName = v.findViewById(R.id.playlist_name);
            playlistSize = v.findViewById(R.id.playlist_size);
        }

        public void bind(MyPlaylist myPlaylist, final int position) {

            playlistName.setText(myPlaylist.getName());

            playlistSize.setText("" + myPlaylist.getSize() + " songs");

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.playMixes(position);
                }
            });


        }
    }
}
