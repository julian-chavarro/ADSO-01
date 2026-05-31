package com.example.adso_01.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.Ejercicio;
import com.example.adso_01.model.Serie;
import com.example.adso_01.viewmodel.RoutineViewModel;

import java.util.List;
import java.util.Locale;

public class ExerciseActiveActivity extends AppCompatActivity {

    private static final long REST_TIME_MS = 90_000L;

    private TextView tvExerciseName, tvMuscles, tvProgress, tvDayTitle, tvCompletedIndicator;
    private Button btnBack, btnPrevious, btnNext;
    private RecyclerView rvSets;
    private SetAdapter adapter;

    private List<Ejercicio> listaEjercicios;
    private int indiceActual;
    private Ejercicio ejercicioActual;

    private RoutineViewModel viewModel;

    private boolean sesionIniciada = false;
    private String grupoMuscular;

    // Estado del modal de descanso
    private AlertDialog restDialog;
    private CountDownTimer restCountDownTimer;
    private boolean restPaused = false;
    private long restTimeRemaining = REST_TIME_MS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_active);

        grupoMuscular = getIntent().getStringExtra("grupo_muscular");

        inicializarVistas();

        viewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(RoutineViewModel.class);

        // Generar la rutina para el grupo muscular seleccionado
        listaEjercicios = viewModel.obtenerRutinaPorGrupoMuscular(grupoMuscular);

        if (listaEjercicios == null || listaEjercicios.isEmpty()) {
            Toast.makeText(this, R.string.error_no_exercises_today, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        indiceActual = 0;

        if (grupoMuscular != null) {
            tvDayTitle.setText(getString(R.string.workout_day_format, grupoMuscular));
        }

        observeViewModel();

        btnBack.setOnClickListener(v -> mostrarDialogoSalir());
        btnPrevious.setOnClickListener(v -> navegarAnterior());
        btnNext.setOnClickListener(v -> navegarSiguiente());

        cargarEjercicio();

        if (!sesionIniciada) {
            sesionIniciada = true;
            viewModel.iniciarSesion(listaEjercicios.size(), grupoMuscular);
        }
    }

    private void observeViewModel() {
        viewModel.getSaveSerieState().observe(this, state -> {
            if (state == null) {
                return;
            }

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                viewModel.clearSaveSerieState();
            }
        });

        viewModel.getSaveSerieSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) {
                return;
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            // Mostrar modal de descanso después de completar una serie
            showRestDialog();
        });
    }

    private void inicializarVistas() {
        tvDayTitle = findViewById(R.id.dayTitle);
        tvExerciseName = findViewById(R.id.exerciseName);
        tvMuscles = findViewById(R.id.muscles);
        tvProgress = findViewById(R.id.exerciseCount);
        tvCompletedIndicator = findViewById(R.id.tvExerciseCompleted);
        rvSets = findViewById(R.id.rvSets);
        btnBack = findViewById(R.id.btnBackToList);
        btnPrevious = findViewById(R.id.btnPreviousExercise);
        btnNext = findViewById(R.id.btnNextExercise);

        rvSets.setLayoutManager(new LinearLayoutManager(this));
    }

    private void cargarEjercicio() {
        ejercicioActual = listaEjercicios.get(indiceActual);

        tvExerciseName.setText(ejercicioActual.getNombre());
        tvMuscles.setText(ejercicioActual.getGrupoMuscular());
        tvProgress.setText(getString(
                R.string.workout_exercise_progress,
                indiceActual + 1,
                listaEjercicios.size()));

        // Estado del botón anterior: deshabilitado en el primer ejercicio
        btnPrevious.setEnabled(indiceActual > 0);

        // Texto del botón siguiente según posición
        if (indiceActual == listaEjercicios.size() - 1) {
            btnNext.setText(R.string.workout_finish_routine);
        } else {
            btnNext.setText(R.string.exercise_next);
        }

        // Crear adapter con las series del ejercicio actual
        adapter = new SetAdapter(ejercicioActual.getSeries(), (serie, weight) ->
                viewModel.saveSerie(ejercicioActual.getNombre(), serie, weight));
        rvSets.setAdapter(adapter);

        // Actualizar indicador visual de ejercicio completado
        actualizarEstadoCompletado();
    }

    // ─── Modal de descanso ───────────────────────────────────────────────

    private void showRestDialog() {
        dismissRestDialog();

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rest_timer, null);
        TextView tvTimer = dialogView.findViewById(R.id.dialogTimer);
        Button btnPause = dialogView.findViewById(R.id.btnPauseRest);
        Button btnSkip = dialogView.findViewById(R.id.btnSkipRest);

        restTimeRemaining = REST_TIME_MS;
        restPaused = false;

        restDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        restDialog.show();

        iniciarTimerDialog(tvTimer, btnPause);

        btnPause.setOnClickListener(v -> {
            if (restPaused) {
                // Reanudar
                restPaused = false;
                btnPause.setText(R.string.rest_dialog_pause);
                iniciarTimerDialog(tvTimer, btnPause);
            } else {
                // Pausar
                restPaused = true;
                btnPause.setText(R.string.rest_dialog_continue);
                if (restCountDownTimer != null) {
                    restCountDownTimer.cancel();
                }
            }
        });

        btnSkip.setOnClickListener(v -> dismissRestDialog());
    }

    private void iniciarTimerDialog(TextView tvTimer, Button btnPause) {
        if (restCountDownTimer != null) {
            restCountDownTimer.cancel();
        }

        restCountDownTimer = new CountDownTimer(restTimeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                restTimeRemaining = millisUntilFinished;
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(ExerciseActiveActivity.this,
                        R.string.rest_complete, Toast.LENGTH_SHORT).show();
                dismissRestDialog();
            }
        }.start();
    }

    private void dismissRestDialog() {
        if (restCountDownTimer != null) {
            restCountDownTimer.cancel();
            restCountDownTimer = null;
        }
        restPaused = false;
        if (restDialog != null && restDialog.isShowing()) {
            restDialog.dismiss();
            restDialog = null;
        }
        // Al cerrar el modal, actualizar el indicador de completado
        actualizarEstadoCompletado();
    }

    // ─── Indicador de ejercicio completado ──────────────────────────────

    private void actualizarEstadoCompletado() {
        if (ejercicioActual == null) {
            tvCompletedIndicator.setVisibility(View.GONE);
            return;
        }
        boolean todasCompletadas = true;
        for (Serie s : ejercicioActual.getSeries()) {
            if (!s.isCompletada()) {
                todasCompletadas = false;
                break;
            }
        }
        tvCompletedIndicator.setVisibility(todasCompletadas ? View.VISIBLE : View.GONE);
    }

    // ─── Navegación entre ejercicios ────────────────────────────────────

    private void navegarAnterior() {
        guardarProgresoEjercicioActual();
        if (indiceActual > 0) {
            indiceActual--;
            cargarEjercicio();
        }
    }

    private void navegarSiguiente() {
        guardarProgresoEjercicioActual();
        if (indiceActual + 1 < listaEjercicios.size()) {
            indiceActual++;
            cargarEjercicio();
        } else {
            viewModel.finalizarSesion();
            Toast.makeText(this, R.string.success_workout_complete, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // ─── Salida y persistencia ──────────────────────────────────────────

    @Override
    public void onBackPressed() {
        mostrarDialogoSalir();
    }

    private void mostrarDialogoSalir() {
        dismissRestDialog();
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_exit_title)
                .setMessage(R.string.confirm_exit_message)
                .setPositiveButton(R.string.confirm_exit_yes, (dialog, which) -> {
                    finish();
                })
                .setNegativeButton(R.string.confirm_exit_no, null)
                .show();
    }

    private void guardarProgresoEjercicioActual() {
        if (ejercicioActual == null) return;
        List<Serie> series = ejercicioActual.getSeries();
        boolean algunaCompletada = false;
        for (Serie s : series) {
            if (s.isCompletada()) {
                algunaCompletada = true;
                break;
            }
        }
        if (algunaCompletada) {
            viewModel.guardarProgresoEjercicio(
                    ejercicioActual.getNombre(),
                    ejercicioActual.getGrupoMuscular(),
                    series);
        }
    }

    @Override
    protected void onDestroy() {
        dismissRestDialog();
        super.onDestroy();
    }
}
