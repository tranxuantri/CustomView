package com.example.customview;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.customview.databinding.ActivityMainBinding;
import com.example.customview.entity.Entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity implements Timer.OnTimeTickListener {
    private final String TAG = "MainActivity";
    ActivityMainBinding mainBinding;
    MediaRecorder mediaRecorder;
    File file;
    private Timer timer;
    private boolean isPause = false;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(mainBinding.getRoot());
        final List<Entry> entryList = new ArrayList<>();
        timer = new Timer(this);
        for (int i = 0; i < 50; i++) {
            Entry entry = new Entry(i, generate_random(0, 1000));
            entryList.add(entry);
        }
        mediaRecorder = new MediaRecorder();

        file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/abc.mp3");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainBinding.recordButton.setOnClickListener(view -> {
            if (!isPause) {
                if (!isRecording) {
                    startRecording();
                } else {
                    pauseRecording();
                }
            } else {
                resumeRecording();
            }
        });
        mainBinding.stopButton.setOnClickListener(view -> {
            stopRecoding();
        });
    }

    public int generate_random(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public void startRecording() {
        mainBinding.recordButton.setText("Pause");
        mainBinding.chart.clear();
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(file);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        timer.start();
    }

    public void pauseRecording() {
        mediaRecorder.pause();
        mainBinding.recordButton.setText("Resume");

        isPause = true;
        isRecording = false;
        timer.pause();
    }

    public void resumeRecording() {
        mediaRecorder.resume();
        isPause = false;
        timer.start();
        isRecording = true;
    }

    private void stopRecoding() {
        isPause = false;
        mediaRecorder.stop();
        timer.stop();
        mainBinding.recordButton.setText("Record");
        Toast.makeText(this, "Record stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerTick(String duration) {
        mainBinding.timerText.setText(duration);
        mainBinding.chart.addAmplitude(mediaRecorder.getMaxAmplitude());
        Log.d(TAG, "onTimerTick: duration " + duration);
    }
}