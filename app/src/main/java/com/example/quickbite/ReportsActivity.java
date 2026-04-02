package com.example.quickbite;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReportsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Toolbar toolbar = findViewById(R.id.reportsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Chronometer chronometer = findViewById(R.id.chronometer);
        // chronometer.setBase(SystemClock.elapsedRealtime());
        // chronometer.start();
    }
}
