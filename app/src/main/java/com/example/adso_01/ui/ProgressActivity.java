package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.adso_01.R;

public class ProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Button btnBack = findViewById(R.id.btnBackProgress);
        btnBack.setOnClickListener(v -> finish());
    }
}
