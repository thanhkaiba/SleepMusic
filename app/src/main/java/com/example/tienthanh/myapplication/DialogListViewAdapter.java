package com.example.tienthanh.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DialogListViewAdapter extends ArrayAdapter<Song> {


    private HashMap<String, MyMediaPlayer> selectedMedia;
    private String[] selectedAudio;
    private Context mContext;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Listener listener;

    public interface Listener {
        void remove(String key);
    }


    public DialogListViewAdapter(Context context, int resource, HashMap<String, MyMediaPlayer> selectedMedia) {
        super(context, resource);
        this.mContext = context;
        this.selectedMedia = selectedMedia;
        selectedAudio = getSelectedAudioList(selectedMedia);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem;

        listItem = LayoutInflater.from(mContext).inflate(R.layout.item_mix_dialog, parent, false);
        String key = selectedAudio[position];
        String[] info = key.split(" ");

        ArrayList<Song> list = new StorageUtil(mContext).loadAudio(info[1]);
        final Song audio = list.get(Integer.parseInt(info[2]));
        final MyMediaPlayer mediaPlayer = selectedMedia.get(info[0]);

        TextView name = listItem.findViewById(R.id.song_name);
        SeekBar volume = listItem.findViewById(R.id.song_volume);
        ImageView remove = listItem.findViewById(R.id.song_remove_from_mix);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.remove(audio.getAID());
                mediaPlayer.release();
                selectedMedia.remove(audio.getAID());
                for (int i = position - 1; i <= 2; i++) {
                    selectedAudio[i] = selectedAudio[i + 1];
                }
                selectedAudio[selectedMedia.size()] = "";
                notifyDataSetChanged();

            }
        });


        volume.setProgress((int) (mediaPlayer.getMyVolume() * 100.0f));
        volume.setMax(100);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.setVolume((float) progress / 100.0f, (float) progress / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        name.setText(audio.getAudioName());

        return listItem;
    }

    @Override
    public int getCount() {
        return selectedMedia.size();
    }

    public static String[] getSelectedAudioList(HashMap<String, MyMediaPlayer> hashMap) {

        String[] result = new String[MediaPlayerService.MAX_PLAYER];
        for (int i = 0; i < 4; i++) {
            result[i] = "";
        }
        int i = 0;
        for (Object o : hashMap.entrySet()) {
            HashMap.Entry m = (HashMap.Entry) o;
            result[i++] = (String) m.getKey();
        }
        return result;
    }
}
