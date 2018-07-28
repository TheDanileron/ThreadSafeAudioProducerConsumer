package com.example.thedanileron.javathreadsaveaudiorecorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Player {

    private static final String LOG_TAG = "RECORDING_APP";

    private MediaPlayer mPlayer;
    private Queue<String> files;
    private int id;
    private Semaphore semaphore;

    public Player(Semaphore semaphore, int id, Queue<String> files) {
        this.semaphore = semaphore;
        this.id = id;
        this.files = files;
    }

    public void startPlaying() {
        while (true) {
            try {
                if (files.isEmpty()) {
                    Thread.sleep(100);
                } else {
                    semaphore.acquire();
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.wtf(LOG_TAG, "Player " + id + " is about to start. Files array : " + files.toString());
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(files.remove());
            mPlayer.prepare();
            mPlayer.setLooping(false);
            mPlayer.start();

            Log.wtf(LOG_TAG, "Player " + id + " started playing...");
            while (mPlayer.getDuration() != mPlayer.getCurrentPosition()) {
                Thread.sleep(100);
            }
            semaphore.release();
            Log.wtf(LOG_TAG, "Player " + id + " finished playing...");
        } catch (IOException e) {
            Log.wtf(LOG_TAG, "Player prepare() failed");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            semaphore.release();
        }
    }

}
