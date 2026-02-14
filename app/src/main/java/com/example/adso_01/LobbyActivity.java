package com.example.adso_01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Button btnObjets = findViewById(R.id.btnObjets);
        Button btnFood = findViewById(R.id.btnFood);
        Button btnStartRoutine = findViewById(R.id.btnStartRoutine);

        btnObjets.setOnClickListener(v ->
                Toast.makeText(LobbyActivity.this,
                        "Sección Objetivos",
                        Toast.LENGTH_SHORT).show()
        );

        btnFood.setOnClickListener(v ->
                Toast.makeText(LobbyActivity.this,
                        "Comidas recomendadas",
                        Toast.LENGTH_SHORT).show()
        );

        btnStartRoutine.setOnClickListener(v ->
                Toast.makeText(LobbyActivity.this,
                        "Empezar rutina",
                        Toast.LENGTH_SHORT).show()
        );
    }
}

