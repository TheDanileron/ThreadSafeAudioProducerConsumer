package com.example.thedanileron.javathreadsaveaudiorecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private Button btnRecord, btnPlay;
    private static volatile BlockingQueue<String> files = new ArrayBlockingQueue<String>(1, true);
    private MainActivity context;

    private AtomicInteger recorderCounter = new AtomicInteger(0);
    private AtomicInteger playerCounter = new AtomicInteger(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        context = this;
        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);

        btnRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Thread threadRecorder = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Recorder recorder = new Recorder(recorderCounter.incrementAndGet() , files, context);
                        recorder.startRecording();
                    }
                });
                threadRecorder.start();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Thread threadPlayer = new Thread(new Runnable() {
                   @Override
                   public void run() {
                        Player player = new Player(playerCounter.incrementAndGet(), files, context);
                        player.startPlaying();
                   }
               });
               threadPlayer.start();
            }
        });
    }


    public void toastMakeText(String message) {
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }
}
