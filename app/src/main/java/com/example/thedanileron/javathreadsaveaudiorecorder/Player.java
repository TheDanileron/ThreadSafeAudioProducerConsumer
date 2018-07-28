package com.example.thedanileron.javathreadsaveaudiorecorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {

    private static final String LOG_TAG = "RECORDING_APP";

    private MediaPlayer mPlayer;
    private BlockingQueue<String> files;
    private Context context;
    private int id;

    public Player(int id, BlockingQueue<String> files, Context context) {
        this.id = id;
        this.files = files;
        this.context = context;
    }

    public void startPlaying() {
        Log.wtf(LOG_TAG, "Player " + id + " about to start. Files array : " + files.toString());
        mPlayer = new MediaPlayer();
        try {
            try {
                mPlayer.setDataSource(files.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mPlayer.prepare();
            mPlayer.start();
            Log.wtf(LOG_TAG, "Player " + id + " started playing...");

        } catch (IOException e) {
            Log.wtf(LOG_TAG, "Player prepare() failed");
        }
    }

}
