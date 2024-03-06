package com.example.customview;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
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
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    MediaRecorder mediaRecorder;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(mainBinding.getRoot());
        final List<Entry> entryList = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            Entry entry = new Entry(i, generate_random(0, 1000));
            entryList.add(entry);
        }
        mainBinding.button.setOnClickListener(view -> mainBinding.chart.setData(entryList));
        mainBinding.stopButton.setEnabled(false);
        mainBinding.playButton.setEnabled(false);
        mediaRecorder = new MediaRecorder();

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/abc.3gp");

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainBinding.recordButton.setOnClickListener(view -> {
            try {
                if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setOutputFile(file);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    mainBinding.recordButton.setEnabled(false);
                    mainBinding.stopButton.setEnabled(true);
                    Toast.makeText(this, "Record starting ...", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        mainBinding.stopButton.setOnClickListener(view -> {
            mediaRecorder.stop();
            mediaRecorder.release();

            mainBinding.recordButton.setEnabled(true);
            mainBinding.playButton.setEnabled(true);
            Toast.makeText(this, "Record stopped", Toast.LENGTH_SHORT).show();
        });

        mainBinding.playButton.setOnClickListener(view -> {
            try {
                entryList.clear();

                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                Log.d("TAG", "mainBinding.playButton.setOnClickListener: " + mediaPlayer.getAudioSessionId());
                Visualizer visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);

                Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {

                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                        int sum = 0;
                        index += 1;
                        for (byte b : waveform) {
                            sum += b;
                        }
                        Log.d("TAG", "onWaveFormDataCapture: waveform" + sum / 2);

                        entryList.add(new Entry(index, Math.abs(sum / 10)));

                        mainBinding.chart.setData(entryList);


                    }

                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

                    }
                };
                Log.d("TAG", "onCreate: Visualizer.getMaxCaptureRate() / 2" + Visualizer.getMaxCaptureRate() / 2);
                visualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() , true, true);
                visualizer.setEnabled(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public int generate_random(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}