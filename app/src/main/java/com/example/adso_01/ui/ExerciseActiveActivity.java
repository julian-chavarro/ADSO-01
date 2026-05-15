package com.example.adso_01.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.Ejercicio;
import com.example.adso_01.viewmodel.RoutineViewModel;

import java.util.List;
import java.util.Locale;

public class ExerciseActiveActivity extends AppCompatActivity {

    private TextView tvExerciseName, tvMuscles, tvTimer, tvProgress, tvDayTitle;
    private Button btnBack, btnStartTimer, btnNext;
    private RecyclerView rvSets;
    private SetAdapter adapter;
    
    private List<Ejercicio> listaEjercicios;
    private int indiceActual;
    private Ejercicio ejercicioActual;
    
    private RoutineViewModel viewModel;
    private CountDownTimer countDownTimer;
    private final long timeLeftInMillis = 90000; // 90 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_active);

        // Recuperar la lista y el índice
        listaEjercicios = (List<Ejercicio>) getIntent().getSerializableExtra("lista_ejercicios");
        indiceActual = getIntent().getIntExtra("indice_actual", 0);
        String diaNombre = getIntent().getStringExtra("dia_nombre");

        if (listaEjercicios == null || listaEjercicios.isEmpty()) {
            finish();
            return;
        }

        inicializarVistas();
        
        if (diaNombre != null) {
            tvDayTitle.setText("Día · " + diaNombre);
        }

        cargarEjercicio();

        viewModel = new ViewModelProvider(this).get(RoutineViewModel.class);

        btnBack.setOnClickListener(v -> finish());
        btnStartTimer.setOnClickListener(v -> startRestTimer());
        
        btnNext.setOnClickListener(v -> {
            if (indiceActual + 1 < listaEjercicios.size()) {
                indiceActual++;
                cargarEjercicio();
            } else {
                Toast.makeText(this, "¡Entrenamiento completado!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void inicializarVistas() {
        tvDayTitle = findViewById(R.id.dayTitle);
        tvExerciseName = findViewById(R.id.exerciseName);
        tvMuscles = findViewById(R.id.muscles);
        tvTimer = findViewById(R.id.timer);
        tvProgress = findViewById(R.id.exerciseCount);
        rvSets = findViewById(R.id.rvSets);
        btnBack = findViewById(R.id.btnBackToList);
        btnStartTimer = findViewById(R.id.startButton);
        btnNext = findViewById(R.id.nextExerciseButton);

        rvSets.setLayoutManager(new LinearLayoutManager(this));
    }

    private void cargarEjercicio() {
        ejercicioActual = listaEjercicios.get(indiceActual);
        
        tvExerciseName.setText(ejercicioActual.getNombre());
        tvMuscles.setText(ejercicioActual.getGrupoMuscular());
        tvProgress.setText("Ejercicio " + (indiceActual + 1) + " de " + listaEjercicios.size());

        if (indiceActual == listaEjercicios.size() - 1) {
            btnNext.setText("Finalizar rutina ✓");
        } else {
            btnNext.setText("Siguiente ejercicio →");
        }

        adapter = new SetAdapter(ejercicioActual.getSeries(), (serie, weight) -> {
            if (weight.isEmpty()) {
                Toast.makeText(this, "Ingresa el peso", Toast.LENGTH_SHORT).show();
                return;
            }
            
            serie.setPeso(Double.parseDouble(weight));
            serie.setCompletada(true);

            viewModel.registrarSerie(ejercicioActual.getNombre(), serie, (success, error) -> {
                if (success) {
                    adapter.notifyDataSetChanged();
                    startRestTimer();
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    serie.setCompletada(false);
                }
            });
        });
        rvSets.setAdapter(adapter);
        
        if (countDownTimer != null) {
            countDownTimer.cancel();
            tvTimer.setText("01:30");
        }
    }

    private void startRestTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(ExerciseActiveActivity.this, "¡Descanso terminado!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
}
