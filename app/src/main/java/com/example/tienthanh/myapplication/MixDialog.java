package com.example.tienthanh.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

public class MixDialog extends Dialog {


    public MixDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.mix_dialog, null);
        setContentView(convertView);
        setTitle("MIX");
        ListView lv = convertView.findViewById(R.id.list_view);

        Button btnAddNewMix = convertView.findViewById(R.id.btn_add_new_mix);
        btnAddNewMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SelectSongMixActivity.class);
                getContext().startActivity(intent);
            }
        });

        SeekBar systemVolume = convertView.findViewById(R.id.system_volume);

        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null) {
            systemVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            systemVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            systemVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }
        if (MainActivity.player != null && MainActivity.player.getNumberOfPlayer() > 0) {
            DialogListViewAdapter adapter = new DialogListViewAdapter(getContext(), 0, MainActivity.player.getSelectedAudios());
            lv.setAdapter(adapter);
        }


    }

}
