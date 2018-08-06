package com.example.tienthanh.myapplication.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tienthanh.myapplication.Activity.MainActivity;
import com.example.tienthanh.myapplication.Adapter.*;

import com.example.tienthanh.myapplication.Model.MyPlaylist;
import com.example.tienthanh.myapplication.Model.StorageUtil;
import com.example.tienthanh.myapplication.R;

import java.util.ArrayList;


public class MixesFragment extends Fragment implements MixesSwipeAdapter.Listener {

    private RecyclerView mixesList;
    private ArrayList<MyPlaylist> listPlayList;

    public MixesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mixes, container, false);
        mixesList = v.findViewById(R.id.mixes_list);
        listPlayList = new StorageUtil(getContext()).loadListOfPlaylist();
        if (listPlayList != null) {
            MixesSwipeAdapter  adapter = new MixesSwipeAdapter (getContext(), listPlayList);
            adapter.setListener(this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mixesList.setLayoutManager(layoutManager);
            mixesList.setAdapter(adapter);
        }
        return v;
    }

    @Override
    public void playMixes(int position) {
        listPlayList = new StorageUtil(getContext()).loadListOfPlaylist();
        MyPlaylist playlist = listPlayList.get(position);
        MainActivity activity = (MainActivity)getActivity();
        activity.updatePlayingSong(playlist.getSongAIDList());
    }
}
