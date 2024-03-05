package com.example.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.customview.databinding.ActivityMainBinding;
import com.example.customview.entity.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
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
    }
    public int generate_random(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}