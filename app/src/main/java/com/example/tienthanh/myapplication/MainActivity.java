package com.example.tienthanh.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    public static CountDownTimer timer;
    public static ArrayList<Song> playingAudio;
    public static MediaPlayerService player;
    private boolean serviceBound = false;
    private BottomNavigationView bottomAppBar;
    private FragmentManager fm;
    private Fragment fragment;

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.tienthanh.myapplication.Broadcast_PLAY_NEW_AUDIO";
    public static final String CATEGORY_URL = "http://192.168.1.182:3002/apimb/v1/categorys/";
    public static final String SONG_URL = "http://192.168.1.182:3002/apimb/v1/audiosByCategory/";
    public static final String SERVER_URL = "http://192.168.1.182:3002";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            playAudio(new StorageUtil(getApplicationContext()).loadAudioLink());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("ServiceState", serviceBound);
        outState.putSerializable("PLAYING_AUDIO", playingAudio);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
        playingAudio = (ArrayList<Song>) savedInstanceState.getSerializable("PLAYING_AUDIO");
        if (playingAudio == null) {
            playingAudio = new ArrayList<>();
        }
    }


    public void initUI() {


        try {
            SeekBar volumeSeekbar = findViewById(R.id.volume_seekbar);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                volumeSeekbar.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                volumeSeekbar.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));
            }


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    if (audioManager != null) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                progress, 0);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {

            fragment = new CategoryFragment();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.navigation_songs:
                    fragment = new AllSongFragment();
                    break;
                case R.id.navigation_playlist:
                    fragment = new PlaylistFragment();
                    break;
                case R.id.navigation_timer:
                    if (timer == null) {
                        fragment = new TimerFragment();
                    } else {
                        fragment = new RunningTimerFragment();
                    }
                    break;
                case R.id.navigation_download:
                    fragment = new DownloadedFragment();
                    break;
            }

            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);


        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();

        bottomAppBar = findViewById(R.id.navigationView);
        bottomAppBar.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
       // initUI();

        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragment = new AllSongFragment();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }


    public void playAudio(String audioLink) {
        if (!serviceBound) {
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioLink(audioLink);
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            playingAudio = new ArrayList<>();
        } else {

            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioLink(audioLink);
            Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
            FloatingActionButton playBack = findViewById(R.id.btn_playback);
            playBack.setImageResource(R.drawable.ic_pause);
        }

    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragment = new TimerFragment();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
            Menu menu = bottomAppBar.getMenu();
            final MenuItem timerView = menu.findItem(R.id.navigation_timer);
            timerView.setTitle("Timer");
        }
    }

    public void stopPlayBack() {
        FloatingActionButton playBack = findViewById(R.id.btn_playback);
        playBack.setImageResource(R.drawable.ic_play);
    }

    public void setTimer(long millisecond) {
        Menu menu = bottomAppBar.getMenu();
        final MenuItem timerView = menu.findItem(R.id.navigation_timer);


        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        timer = new CountDownTimer(millisecond, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                String timeString = formatter.format(new Date(millisUntilFinished));
                fragment = fm.findFragmentById(R.id.container);
                if (fragment instanceof RunningTimerFragment) {
                    RunningTimerFragment runningTimerFragment = (RunningTimerFragment) fragment;
                    runningTimerFragment.initUI(timeString);
                }
                timerView.setTitle(timeString);
            }


            @Override
            public void onFinish() {
                timer = null;
                timerView.setTitle("Timer");

                if (player != null) {
                    player.pauseMedia();
                }
                fragment = fm.findFragmentById(R.id.container);
                if (fragment instanceof RunningTimerFragment) {
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragment = new TimerFragment();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.commit();
                }
            }
        };
        timer.start();

    }


    public void onClickPlayBack(View view) {
        FloatingActionButton playBack = (FloatingActionButton) view;
        if (player != null && playingAudio != null && playingAudio.size() > 0) {
            if (player.isPlaying()) {
                player.pauseMedia();
                playBack.setImageResource(R.drawable.ic_play);
            } else {
                player.resumeMedia();
                playBack.setImageResource(R.drawable.ic_pause);
            }
        } else {
            playBack.setImageResource(R.drawable.ic_play);
        }
    }
}
