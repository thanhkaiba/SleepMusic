package com.example.tienthanh.myapplication.Service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.tienthanh.myapplication.Activity.MainActivity;
import com.example.tienthanh.myapplication.Model.MyMediaPlayer;
import com.example.tienthanh.myapplication.Model.PlaybackStatus;

import com.example.tienthanh.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MediaPlayerService extends Service implements
        MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener

{
    public static final int MAX_PLAYER = 8;


    public static final String ACTION_PLAY = "com.example.tienthanh.myapplication.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.tienthanh.myapplication.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.example.tienthanh.myapplication.ACTION_STOP";

    private static final int NOTIFICATION_ID = 101;

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

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

            String newAudioLink = intent.getStringExtra(MainActivity.AUDIO_LINK);
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
                buildNotification(PlaybackStatus.PLAYING);

            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Some thing went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        register_playNewAudio();
        registerBecomingNoisyReceiver();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        requestAudioFocus();
        initMediaSession();
        handleIncomingActions(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMedia();
        removeAudioFocus();
        removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(addNewMix);
    }

   @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (!isPlaying())
                    resumeMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (isPlaying()) stopMedia();
                resumeMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isPlaying()) pauseMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (isPlaying()) pauseMedia();
                break;
        }
    }

    public boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = 0;
        if (audioManager != null) {
            result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

    }

    public void removeAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }



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

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
        }
    };

    private void registerBecomingNoisyReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }


    private void initMediaSession()  {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();

                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();

                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void buildNotification(PlaybackStatus playbackStatus) {


        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.chaien); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this, "ID")
                // Hide the timestamp
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compat view
                        .setShowActionsInCompactView())
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorAccent))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText("CONTENT")
                .setContentTitle("TEST")
                .setContentInfo("TITLE")
                // Add playback actions
                .addAction(notificationAction, "pause", play_pauseAction);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.chaien); //replace with medias albumArt
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "ARTIST")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "ALBUM")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "TITLE")
                .build());
    }


    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);

                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause

                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        }
    }

}
