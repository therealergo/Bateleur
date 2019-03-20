package com.bateleur.app.model;

import com.bateleur.app.datatype.BAudio;

public class PlaybackModel {

    private float playbackVolume;

    private long getPlaybackLengthMS;

    public float getPlaybackVolume() {
        return playbackVolume;
    }

    public void setPlaybackVolume(float playbackVolume) {
        this.playbackVolume = playbackVolume;
    }

    public long getGetPlaybackLengthMS() {
        return getPlaybackLengthMS;
    }

    public void setGetPlaybackLengthMS(long getPlaybackLengthMS) {
        this.getPlaybackLengthMS = getPlaybackLengthMS;
    }

    public void play(BAudio audio, int fadeTimeMS) {

    }

    public void play(int fadeTimesMS) {

    }

    public void pause(int fadeTimeMS) {

    }

}
