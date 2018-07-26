package com.example.tienthanh.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    public static MediaPlayerService player;
    private boolean serviceBound = false;
    public static String Broadcast_PLAY_NEW_AUDIO = "com.example.tienthanh.myapplication.Broadcast_PLAY_NEW_AUDIO";
    private BottomNavigationView bottomAppBar;
    private FragmentManager fm;
    private Fragment fragment;


    public static final String CATEGORY_URL = "http://192.168.1.182:3002/apimb/v1/categorys/";
    public static final String SONG_URL = "http://192.168.1.182:3002/apimb/v1/audiosByCategory/";
    public static final String SERVER_URL = "http://192.168.1.182:3002";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            playAudio(new StorageUtil(getApplicationContext()).loadAudioIndex());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }


    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            fragment = new CategoryFragment();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.navigation_songs:
                    fragment = new CategoryFragment();
                    break;
                case R.id.navigation_playlist:
                    fragment = new PlaylistFragment();
                    break;
                case R.id.navigation_timer:
                    fragment = new TimerFragment();
                    break;
                case R.id.navigation_download:
                    fragment = new DownloadedFragment();
                    break;
            }

            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();

            return true;
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();

        bottomAppBar = findViewById(R.id.navigationView);
        bottomAppBar.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);

        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragment = new CategoryFragment();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }

    @Override
    public void onBackPressed() {

        fragment = fm.findFragmentById(R.id.container);

        if (fragment instanceof SongListFragment) {
            fragment = new CategoryFragment();
            fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
        } else
            super.onBackPressed();
    }

    public void playAudio(int audioIndex) {

        if (!serviceBound) {

            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {

            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);
            Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
            Intent intent = new Intent(this, SongActivity.class);
            startActivity(intent);
        }

    }
}
