package com.example.tienthanh.myapplication.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tienthanh.myapplication.Activity.MainActivity;
import com.example.tienthanh.myapplication.Adapter.DialogListViewAdapter;
import com.example.tienthanh.myapplication.Model.MyPlaylist;
import com.example.tienthanh.myapplication.Model.Song;
import com.example.tienthanh.myapplication.Model.StorageUtil;
import com.example.tienthanh.myapplication.R;

import java.util.ArrayList;

public class MixDialog extends Dialog {

    private DialogListViewAdapter adapter;
    private EditText playlistName;
    private TextView clearMix;
    private TextView tvPlaylistName;
    private TextView saveMix;
    private ListView lv;

    public MixDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.mix_dialog, null);
        setContentView(convertView);
        setTitle("MIX");

        playlistName = findViewById(R.id.playlist_name);
        tvPlaylistName = findViewById(R.id.tv_playlist_name);
        lv = convertView.findViewById(R.id.list_view);
        clearMix = findViewById(R.id.tv_clear_mix);
        saveMix = findViewById(R.id.tv_save_mix);

        saveMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lv.getVisibility() == View.VISIBLE) {


                    playlistName.setVisibility(View.VISIBLE);

                    playlistName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(playlistName, InputMethodManager.SHOW_IMPLICIT);
                    }

                    tvPlaylistName.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.INVISIBLE);
                    clearMix.setText("CANCEL");
                } else {
                    ArrayList<MyPlaylist> listPlayList = new StorageUtil(getContext()).loadListOfPlaylist();
                    if (listPlayList == null) {
                        listPlayList = new ArrayList<>();
                    }
                    MyPlaylist newPlaylist = new MyPlaylist(playlistName.getText().toString(), MainActivity.playingAudio.size(), MyPlaylist.getAIDList(MainActivity.playingAudio));
                   if (!listPlayList.contains(newPlaylist)) {
                       listPlayList.add(newPlaylist);
                       new StorageUtil(getContext()).storeListOfPlaylist(listPlayList);

                      dismiss();
                   } else {
                       Toast.makeText(getContext(), "Name is already used by existing playlist", Toast.LENGTH_SHORT).show();
                   }

                }


            }
        });

        clearMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lv.getVisibility() == View.VISIBLE) {
                    for (Song song : MainActivity.playingAudio) {
                        MainActivity.player.removeSelectedAudioFromMix(MainActivity.SERVER_URL + song.getAudioLink());
                    }
                    MainActivity.playingAudio.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    clearMix.setText("CLEAR");
                    tvPlaylistName.setVisibility(View.INVISIBLE);
                    playlistName.setVisibility(View.INVISIBLE);
                    lv.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                }
            }
        });

        if (MainActivity.player != null && MainActivity.player.getNumberOfPlayer() > 0) {
            adapter = new DialogListViewAdapter(getContext(), 0, MainActivity.player.getSelectedAudios());
            lv.setAdapter(adapter);
        }
    }

}
