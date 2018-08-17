package com.example.tienthanh.myapplication.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import com.example.tienthanh.myapplication.Fragment.AllSongFragment;
import com.example.tienthanh.myapplication.Fragment.MixesFragment;
import com.example.tienthanh.myapplication.Fragment.RunningTimerFragment;
import com.example.tienthanh.myapplication.Fragment.TimerFragment;
import com.example.tienthanh.myapplication.Service.MediaPlayerService;
import com.example.tienthanh.myapplication.Dialog.MixDialog;
import com.example.tienthanh.myapplication.Model.Song;
import com.example.tienthanh.myapplication.Model.StorageUtil;
import com.example.tienthanh.myapplication.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    public static MediaPlayerService player;
    public static ArrayList<Song> playingAudio;
    private static Fragment currentFragment;
    private RewardedVideoAd mAd;

    private MixDialog mixDialog;
    private CountDownTimer timer;
    private AudioManager audioManager;
    private SeekBar volumeSeekbar;
    private FloatingActionButton playBack;
    private boolean serviceBound = false;
    private BottomNavigationView bottomAppBar;
    private FragmentManager fm;


    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.tienthanh.myapplication.Broadcast_PLAY_NEW_AUDIO";
    public static final String CATEGORY_URL = "http://192.168.1.182:3002/apimb/v1/categorys/";
    public static final String SONG_URL = "http://192.168.1.182:3002/apimb/v1/audiosByCategory/";
    public static final String SERVER_URL = "http://192.168.1.182:3002";
    public static final String AUDIO_LINK = "Audio link";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }



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
        } else if (player.isPlaying()){
            playBack.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            super.onKeyDown(keyCode, event);
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }




    @Override
    protected void onPause() {
        super.onPause();
        mixDialog = null;
    }

    public void initUI() {


        try {

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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

            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            Fragment fragment = new AllSongFragment();
            switch (menuItem.getItemId()) {
                case R.id.navigation_songs:
                    fragment = new AllSongFragment();
                    break;
                case R.id.navigation_mixer:
                    if (playingAudio.size() > 0 && mixDialog == null) {
                        mixDialog = new MixDialog(MainActivity.this);
                        mixDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Fragment f = fm.findFragmentById(R.id.container);
                                if (f instanceof AllSongFragment) {
                                    mixDialog = null;
                                    displayFragment(new AllSongFragment());
                                }
                            }
                        });
                        mixDialog.show();
                    } else if (playingAudio == null || playingAudio.size() == 0) {
                        Toast.makeText(MainActivity.this, "No songs selected!", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                case R.id.navigation_timer:
                    if (timer == null) {
                        fragment = new TimerFragment();
                    } else {
                        fragment = new RunningTimerFragment();
                    }
                    break;
                case R.id.navigation_mixes:
                    fragment = new MixesFragment();
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
        setContentView(R.layout.activity_main);

        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        playBack = findViewById(R.id.btn_playback);
        bottomAppBar = findViewById(R.id.navigationView);
        volumeSeekbar = findViewById(R.id.volume_seekbar);
        bottomAppBar.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        initUI();
        playingAudio = new ArrayList<>();
        fm = getSupportFragmentManager();
        displayFragment(new AllSongFragment());

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);




      ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("KAIBA", "TEST");
                        mAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

                    }
                });

            }
        }, 30, 240, TimeUnit.SECONDS);


    }



    public void playAudio(String audioLink) {

        Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO);
        broadcastIntent.putExtra(AUDIO_LINK, audioLink);
        sendBroadcast(broadcastIntent);
        playBack.setImageResource(R.drawable.ic_pause);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            displayFragment(new TimerFragment());
            Menu menu = bottomAppBar.getMenu();
            final MenuItem timerView = menu.findItem(R.id.navigation_timer);
            timerView.setTitle("Timer");
        }
    }

    public void stopPlayBack() {
        playBack.setImageResource(R.drawable.ic_play);
    }

    public void setTimer(long millisecond) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Menu menu = bottomAppBar.getMenu();
        final MenuItem timerView = menu.findItem(R.id.navigation_timer);


        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        timer = new CountDownTimer(millisecond, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                String timeString = formatter.format(new Date(millisUntilFinished));
                Fragment fragment = fm.findFragmentById(R.id.container);
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
                    playBack.setImageResource(R.drawable.ic_play);
                }
                Fragment fragment = fm.findFragmentById(R.id.container);
                if (fragment instanceof RunningTimerFragment) {
                    displayFragment(new TimerFragment());
                }
            }
        };
        timer.start();
    }

    private void displayFragment(Fragment f) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, f);
        fragmentTransaction.commit();
    }


    public void updatePlayingSong(ArrayList<String> listSongAID) {

        ArrayList<String> listSongLink = new ArrayList<>();

        ArrayList<Song> allSongList = new StorageUtil(getApplicationContext()).loadAllSongList();
        player.releaseMedia();
        playingAudio.clear();
        Song song = new Song();
        for (String AID : listSongAID) {
            song.setAID(AID);
            int index = allSongList.indexOf(song);
            listSongLink.add(allSongList.get(index).getAudioMp3());
            playingAudio.add(allSongList.get(index));
        }
        player.updateSelectedMedia(listSongLink);
        displayFragment(new AllSongFragment());
        playBack.setImageResource(R.drawable.ic_pause);
    }

    public void clearMix() {
        player.releaseMedia();
        playingAudio.clear();
        playBack.setImageResource(R.drawable.ic_play);

    }



    public void onClickPlayBack(View view) {

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



    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (currentFragment != null) {
          displayFragment(currentFragment);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
