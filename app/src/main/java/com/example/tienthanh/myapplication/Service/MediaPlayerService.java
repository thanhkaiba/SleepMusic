package com.example.tienthanh.myapplication.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.tienthanh.myapplication.Activity.MainActivity;
import com.example.tienthanh.myapplication.Model.MyMediaPlayer;
import com.example.tienthanh.myapplication.Model.Song;
import com.example.tienthanh.myapplication.Model.StorageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MediaPlayerService extends Service implements
        MediaPlayer.OnErrorListener

{
    public static final int MAX_PLAYER = 8;

    private final IBinder iBinder = new LocalBinder();

    private HashMap<String, MyMediaPlayer> selectedAudios = new HashMap<>();
    private AudioManager audioManager;

    public int getNumberOfPlayer() {
        return numberOfPlayer;
    }

    public int numberOfPlayer;

    public HashMap<String, MyMediaPlayer> getSelectedAudios() {

        return selectedAudios;
    }



    private void register_playNewAudio() {
        IntentFilter filter = new IntentFilter(MainActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(addNewMix, filter);
    }

    private BroadcastReceiver addNewMix = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String newAudioLink = new StorageUtil(getApplicationContext()).loadAudioLink();

            playMedia(newAudioLink);
        }

    };

    private void playMedia(String newAudioLink) {
        if (numberOfPlayer < MAX_PLAYER) {
            final MyMediaPlayer mix = new MyMediaPlayer();
            try {
                mix.setDataSource(newAudioLink);
                mix.prepareAsync();
                mix.setVolume(0.7f, 0.7f);
                mix.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mix.start();
                    }
                });
                mix.setLooping(true);
                numberOfPlayer++;
                selectedAudios.put(newAudioLink, mix);

            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Some thing went wrong!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        register_playNewAudio();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMedia();
        /*removeAudioFocus();*/
    }

  /*  @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mediaPlayer == null) initMyMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }*/

 /*   public boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = 0;
        if (audioManager != null) {
            result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

    }

    public void removeAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }*/


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MyMediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MyMediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MyMediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }



    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }


    public boolean isPlaying() {
        for (Object o : selectedAudios.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            MyMediaPlayer m = (MyMediaPlayer) pair.getValue();
            if (m.isPlaying()) {
                return true;
            }
        }
        return false;
    }

    public void resumeMedia() {
        for (Object o : selectedAudios.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            MyMediaPlayer m = (MyMediaPlayer) pair.getValue();
            if (!m.isPlaying()) {
                m.resume();
            }
        }
    }

    public void updateSelectedMedia(ArrayList<String> listSongLink) {
        releaseMedia();
        for (String audioLink : listSongLink) {
            playMedia(audioLink);
        }
    }

    private void stopMedia() {

        for (Object o : selectedAudios.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            MediaPlayer m = (MediaPlayer) pair.getValue();
            if (m.isPlaying()) {
                m.stop();
            }
        }
    }

    public void releaseMedia() {

        for (Object o : selectedAudios.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            MediaPlayer m = (MediaPlayer) pair.getValue();
            m.release();
        }
        numberOfPlayer = 0;
        selectedAudios.clear();

    }


    public void pauseMedia() {
        for (Object o : selectedAudios.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            MediaPlayer m = (MediaPlayer) pair.getValue();
            if (m.isPlaying()) {
                m.pause();
            }
        }
    }

    public void removeSelectedAudioFromMix(String key) {
        MediaPlayer m = selectedAudios.get(key);
       if (m!= null) {
           m.stop();
           m.release();
           selectedAudios.remove(key);
           numberOfPlayer--;
       }
    }

}
