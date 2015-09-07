package com.uxcasuals.waves.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.widget.Toast;

import com.uxcasuals.waves.models.RadioStation;

import java.io.IOException;


public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "MusicService";
    private static final String WIFI_TAG = "WIFI_LOCK";

    MediaPlayer mMediaPlayer = null;
    WifiManager.WifiLock wifiLock;
    RadioStation station = new RadioStation();

    public static final int MEDIA_PLAYER_INIT = 0;
    public static final int MEDIA_PLAYER_PLAY = 1;
    public static final int MEDIA_PLAYER_PAUSE = 2;
    public static final int MEDIA_PLAYER_RESUME = 3;
    public static final int MEDIA_PLAYER_STOP = 4;
    public static final int MEDIA_PLAYER_RELEASE = 5;

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEDIA_PLAYER_INIT:
                    Toast.makeText(getApplicationContext(), "Hello, Music Lovers!!", Toast.LENGTH_SHORT).show();
                    initMediaPlayer();
                    break;
                
                case MEDIA_PLAYER_PLAY:
                    Bundle data = msg.getData();
                    station.setName((String) data.get("name"));
                    station.setUrl((String) data.get("url"));

                    startMediaPlayer();
                    break;

                case MEDIA_PLAYER_PAUSE:
                    pauseMediaPlayer();
                    break;

                case MEDIA_PLAYER_RESUME:
                    resumeMediaPlayer();
                    break;

                case MEDIA_PLAYER_STOP:
                    stopMediaPlayer();
                    break;

                case MEDIA_PLAYER_RELEASE:
                    releaseMediaPlayer();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void initMediaPlayer() {
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, MusicService.WIFI_TAG);
            wifiLock.acquire();

            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
        }
    }

    private void startMediaPlayer() {
        if(mMediaPlayer == null) {
            initMediaPlayer();
        }
        if(mMediaPlayer != null) {
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(station.getUrl());
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseMediaPlayer() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    private void resumeMediaPlayer() {
        if(mMediaPlayer !=null && !mMediaPlayer.isPlaying()) {

        }
    }

    private void stopMediaPlayer() {
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            wifiLock.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    final Messenger messenger = new Messenger(new MessageHandler());

    @Override
    public void onPrepared(MediaPlayer mMediaPlayer) {
        Toast.makeText(getApplicationContext(), "Playin " + station.getName(), Toast.LENGTH_SHORT).show();
        mMediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Problem with streaming. Try other stations!!", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
