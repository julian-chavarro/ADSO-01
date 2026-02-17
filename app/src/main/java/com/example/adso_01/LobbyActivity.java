package com.example.adso_01;

import android.content.Intent;
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
        Button btnChatBubble = findViewById(R.id.btnChatBubble);

        btnObjets.setOnClickListener(v ->
                Toast.makeText(this, "Sección Objetivos", Toast.LENGTH_SHORT).show());

        btnFood.setOnClickListener(v ->
                Toast.makeText(this, "Comidas recomendadas", Toast.LENGTH_SHORT).show());

        btnStartRoutine.setOnClickListener(v ->
                Toast.makeText(this, "Empezar rutina", Toast.LENGTH_SHORT).show());

        // 🔑 BURBUJA → ABRIR CHAT COMPLETO
        btnChatBubble.setOnClickListener(v -> {
            Intent intent = new Intent(LobbyActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }
}
