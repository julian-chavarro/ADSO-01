package com.example.adso_01.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.Ejercicio;
import com.example.adso_01.viewmodel.RoutineViewModel;
import com.example.adso_01.viewmodel.UserViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoutineActivity extends AppCompatActivity {

    private RecyclerView rvExercises;
    private ExerciseAdapter adapter;
    private RoutineViewModel routineViewModel;
    private UserViewModel userViewModel;
    private List<Ejercicio> exerciseList = new ArrayList<>();
    private Button btnBackToLobby, btnStartTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        rvExercises = findViewById(R.id.rvExercises);
        btnBackToLobby = findViewById(R.id.btnBackToLobby);
        btnStartTraining = findViewById(R.id.btnStartTraining);

        rvExercises.setLayoutManager(new LinearLayoutManager(this));

        routineViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        adapter = new ExerciseAdapter(exerciseList, ejercicio -> {
            int index = exerciseList.indexOf(ejercicio);
            irAEjercicioActivo(index);
        });
        rvExercises.setAdapter(adapter);

        btnBackToLobby.setOnClickListener(v -> finish());

        btnStartTraining.setOnClickListener(v -> {
            if (!exerciseList.isEmpty()) {
                irAEjercicioActivo(0);
            } else {
                Toast.makeText(this, "No hay ejercicios cargados para hoy", Toast.LENGTH_SHORT).show();
            }
        });

        cargarRutina();
    }

    private void irAEjercicioActivo(int index) {
        Intent intent = new Intent(this, ExerciseActiveActivity.class);
        intent.putExtra("lista_ejercicios", (Serializable) exerciseList);
        intent.putExtra("indice_actual", index);
        startActivity(intent);
    }

    private void cargarRutina() {
        String diaSeleccionado = getIntent().getStringExtra("dia_seleccionado");

        userViewModel.obtenerUsuario((usuario, error) -> {
            if (usuario != null) {
                String objetivo = usuario.getObjetivo();
                exerciseList.clear();
                exerciseList.addAll(routineViewModel.obtenerRutinaSugerida(objetivo, diaSeleccionado));
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Primero configura tus objetivos", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
