package com.example.adso_01.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.adso_01.R;

/**
 * Pantalla de selección de grupo muscular.
 * Redirige directamente al entrenamiento activo con la rutina del grupo seleccionado.
 */
public class RoutineSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_selection);

        setupMuscleGroupButton(R.id.btnDay1, "Pecho");
        setupMuscleGroupButton(R.id.btnDay2, "Espalda");
        setupMuscleGroupButton(R.id.btnDay3, "Pierna");
        setupMuscleGroupButton(R.id.btnDay4, "Hombro");
        setupMuscleGroupButton(R.id.btnDay5, "Full Body");

        findViewById(R.id.btnBackSelection).setOnClickListener(v -> finish());
    }

    private void setupMuscleGroupButton(int buttonId, String grupoMuscular) {
        Button btn = findViewById(buttonId);
        if (btn != null) {
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(this, ExerciseActiveActivity.class);
                intent.putExtra("grupo_muscular", grupoMuscular);
                startActivity(intent);
            });
        }
    }
}
