package com.example.tienthanh.myapplication;

import android.media.MediaPlayer;

public class MyMediaPlayer extends MediaPlayer{

    private float myVolume;
    private int resumePotion;

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        super.setVolume(leftVolume, rightVolume);
        this.myVolume = leftVolume;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        resumePotion = getCurrentPosition();
    }

    public void resume() {
        seekTo(resumePotion);
        start();
    }

    public float getMyVolume() {
        return myVolume;
    }
}
