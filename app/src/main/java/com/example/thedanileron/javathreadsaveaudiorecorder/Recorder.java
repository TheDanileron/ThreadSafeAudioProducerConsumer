package com.example.thedanileron.javathreadsaveaudiorecorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class Recorder {
    private static final String LOG_TAG = "RECORDING_APP";

    private MediaRecorder mRecorder;
    private Queue<String> files;
    private Semaphore semaphore;
    private int id;

    public Recorder(Semaphore semaphore, int id, Queue<String> files) {
        this.semaphore = semaphore;
        this.id = id;
        this.files = files;
    }

    protected void startRecording() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File mOutputFile = getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        final String fPath = mOutputFile.getAbsolutePath();

        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mOutputFile.getAbsolutePath());
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();

            Log.wtf(LOG_TAG, "Recorder " + id + " started recording...");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Recorder prepare() failed");
            semaphore.release();
        }

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            stopRecording();
                            files.add(fPath);
                        } finally {
                            semaphore.release();
                        }
                    }
                },
                10000);
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;

        Log.wtf(LOG_TAG, "Recorder " + id + " stopped recording...");
    }

    private File getOutputFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath().toString()
                + "/Voice Recorder/RECORDING_"
                + dateFormat.format(new Date())
                + ".3gp");
    }
}
