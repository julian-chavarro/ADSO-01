package com.example.adso_01.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.adso_01.R;
import com.example.adso_01.model.Ejercicio;
import com.example.adso_01.viewmodel.RoutineViewModel;
import com.example.adso_01.viewmodel.UserViewModel;
import java.io.Serializable;
import java.util.List;

public class RoutineSelectionActivity extends AppCompatActivity {

    private RoutineViewModel routineViewModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_selection);

        routineViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        findViewById(R.id.btnDay1).setOnClickListener(v -> iniciarEntrenamiento("Lunes"));
        findViewById(R.id.btnDay2).setOnClickListener(v -> iniciarEntrenamiento("Martes"));
        findViewById(R.id.btnDay3).setOnClickListener(v -> iniciarEntrenamiento("Miércoles"));
        findViewById(R.id.btnDay4).setOnClickListener(v -> iniciarEntrenamiento("Jueves"));
        findViewById(R.id.btnDay5).setOnClickListener(v -> iniciarEntrenamiento("Viernes"));
        findViewById(R.id.btnBackSelection).setOnClickListener(v -> finish());
    }

    private void iniciarEntrenamiento(String dia) {
        userViewModel.obtenerUsuario((usuario, error) -> {
            if (usuario != null) {
                List<Ejercicio> rutina = routineViewModel.obtenerRutinaSugerida(usuario.getObjetivo(), dia);
                if (!rutina.isEmpty()) {
                    Intent intent = new Intent(this, ExerciseActiveActivity.class);
                    intent.putExtra("lista_ejercicios", (Serializable) rutina);
                    intent.putExtra("indice_actual", 0);
                    intent.putExtra("dia_nombre", dia);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No hay ejercicios para este día", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Configura tus objetivos primero", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
