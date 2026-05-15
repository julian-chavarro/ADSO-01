package com.example.adso_01.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.viewmodel.UserViewModel;

public class LobbyActivity extends AppCompatActivity {

    Button btnObjets, btnFood, btnStartRoutine, btnProgress, btnChatBubble;
    TextView txtObjetivoUsuario, txtCaloriasUsuario;
    LinearLayout cardObjetivo;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        btnObjets = findViewById(R.id.btnObjets);
        btnFood = findViewById(R.id.btnFood);
        btnStartRoutine = findViewById(R.id.btnStartRoutine);
        btnProgress = findViewById(R.id.btnProgress);
        btnChatBubble = findViewById(R.id.btnChatBubble);
        cardObjetivo = findViewById(R.id.cardObjetivo);
        txtObjetivoUsuario = findViewById(R.id.txtObjetivoUsuario);
        txtCaloriasUsuario = findViewById(R.id.txtCaloriasUsuario);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        cargarObjetivos();

        // Al pulsar el botón amarillo, vamos a la SELECCIÓN de rutina
        btnStartRoutine.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoutineSelectionActivity.class);
            startActivity(intent);
        });

        btnObjets.setOnClickListener(v -> startActivity(new Intent(this, ObjetivoActivity.class)));
        btnFood.setOnClickListener(v -> startActivity(new Intent(this, NutritionActivity.class)));
        btnProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        btnChatBubble.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
    }

    private void cargarObjetivos() {
        userViewModel.obtenerUsuario((usuario, error) -> {
            if (error != null) return;
            if (usuario == null) {
                cardObjetivo.setVisibility(View.GONE);
                return;
            }
            cardObjetivo.setVisibility(View.VISIBLE);
            txtObjetivoUsuario.setText("Objetivo: " + usuario.getObjetivo());
            txtCaloriasUsuario.setText("Calorías recomendadas: " + usuario.getCalorias() + " kcal");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarObjetivos();
    }
}
