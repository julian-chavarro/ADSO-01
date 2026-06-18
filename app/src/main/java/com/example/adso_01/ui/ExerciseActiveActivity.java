package com.example.adso_01.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.Ejercicio;
import com.example.adso_01.model.Serie;
import com.example.adso_01.viewmodel.RoutineViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.List;
import java.util.Locale;

/**
 * Actividad principal de entrenamiento activo.
 * <p>
 * Gestiona la experiencia de entrenamiento en vivo: muestra un ejercicio
 * a la vez con sus series correspondientes en un {@link RecyclerView},
 * permite navegar entre ejercicios, guarda el progreso de cada serie en
 * Firestore, y muestra un modal de descanso de 90 segundos entre series.
 * </p>
 *
 * <h3>Flujo de navegación:</h3>
 * <ol>
 *   <li>Recibe el grupo muscular desde {@link RoutineSelectionActivity}.</li>
 *   <li>Genera la rutina ({@link Ejercicio}[]) para ese grupo.</li>
 *   <li>Itera ejercicios uno por uno con botones Anterior/Siguiente.</li>
 *   <li>Al completar el último ejercicio, redirige a {@link WorkoutSummaryActivity}.</li>
 * </ol>
 */
@AndroidEntryPoint
public class ExerciseActiveActivity extends AppCompatActivity {

    /** Duración del temporizador de descanso: 90 segundos. */
    private static final long REST_TIME_MS = 90_000L;

    /** Claves para preservar el estado en {@link #onSaveInstanceState}. */
    private static final String KEY_INDICE_ACTUAL = "indice_actual";
    private static final String KEY_SESION_INICIADA = "sesion_iniciada";
    private static final String KEY_GRUPO_MUSCULAR = "grupo_muscular";
    private static final String KEY_SESSION_START_TIME = "session_start_time";
    private static final String KEY_REST_TIME = "rest_time_remaining";
    private static final String KEY_REST_PAUSED = "rest_paused";

    // ─── Vistas ──────────────────────────────────────────────────────────

    private TextView tvExerciseName, tvMuscles, tvProgress, tvDayTitle, tvCompletedIndicator;
    private Button btnBack, btnPrevious, btnNext;
    private RecyclerView rvSets;
    private SetAdapter adapter;
    private Button videoButton;
    private String consejos;  // tips técnicos para el usuario

    // ─── Estado de la sesión ─────────────────────────────────────────────

    /** Lista completa de ejercicios para este grupo muscular. */
    private List<Ejercicio> listaEjercicios;

    /** Índice del ejercicio actual dentro de {@link #listaEjercicios}. */
    private int indiceActual;

    /** Ejercicio actualmente visible. */
    private Ejercicio ejercicioActual;

    /** ViewModel que gestiona la lógica de rutinas y persistencia. */
    private RoutineViewModel viewModel;

    /** Indica si la sesión ya fue registrada (evita duplicados en onCreate). */
    private boolean sesionIniciada = false;

    /** Grupo muscular recibido del Intent. */
    private String grupoMuscular;

    // ─── Modal de descanso ──────────────────────────────────────────────

    private AlertDialog restDialog;
    private CountDownTimer restCountDownTimer;
    private boolean restPaused = false;
    private long restTimeRemaining = REST_TIME_MS;

    // ─── Temporización de sesión ─────────────────────────────────────────

    /** Timestamp de inicio (System.currentTimeMillis) para calcular duración. */
    private long sessionStartTime;

    // ═════════════════════════════════════════════════════════════════════
    //  Ciclo de vida
    // ═════════════════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_active);


        grupoMuscular = getIntent().getStringExtra("grupo_muscular");
        inicializarVistas();

        viewModel = new ViewModelProvider(this).get(RoutineViewModel.class);

        // Obtener sexo y objetivo del usuario para rutinas diferenciadas
        String sexo = getIntent().getStringExtra("sexo");
        String objetivo = getIntent().getStringExtra("objetivo");

        // Obtener la lista de ejercicios para el grupo muscular seleccionado
        listaEjercicios = viewModel.obtenerRutinaPorGrupoMuscular(grupoMuscular, sexo, objetivo);
        if (listaEjercicios == null || listaEjercicios.isEmpty()) {
            Toast.makeText(this, R.string.error_no_exercises_today, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Restaurar estado si viene de una rotación de pantalla
        if (savedInstanceState != null) {
            indiceActual = savedInstanceState.getInt(KEY_INDICE_ACTUAL, 0);
            sesionIniciada = savedInstanceState.getBoolean(KEY_SESION_INICIADA, false);
            sessionStartTime = savedInstanceState.getLong(KEY_SESSION_START_TIME, System.currentTimeMillis());
            restTimeRemaining = savedInstanceState.getLong(KEY_REST_TIME, REST_TIME_MS);
            restPaused = savedInstanceState.getBoolean(KEY_REST_PAUSED, false);
        } else {
            indiceActual = 0;
            sesionIniciada = false;
            sessionStartTime = System.currentTimeMillis();
            restTimeRemaining = REST_TIME_MS;
            restPaused = false;
        }

        if (grupoMuscular != null) {
            tvDayTitle.setText(getString(R.string.workout_day_format, grupoMuscular));
        }

        observeViewModel();

        btnBack.setOnClickListener(v -> {
            dismissRestDialog();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_exit_title)
                    .setMessage(R.string.confirm_exit_message)
                    .setPositiveButton(R.string.confirm_exit_yes, (dialog, which) -> {
                        Intent intent = new Intent(this, RoutineSelectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
                        finish();
                    })
                    .setNegativeButton(R.string.confirm_exit_no, null)
                    .show();
        });
        btnPrevious.setOnClickListener(v -> navegarAnterior());
        btnNext.setOnClickListener(v -> navegarSiguiente());
        videoButton.setOnClickListener(v -> mostrarVideoEjercicio(ejercicioActual));

        cargarEjercicio();

        // Iniciar la sesión en Firestore si no está iniciada
        if (!sesionIniciada) {
            sesionIniciada = true;
            sessionStartTime = System.currentTimeMillis();
            viewModel.iniciarSesion(listaEjercicios.size(), grupoMuscular);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDICE_ACTUAL, indiceActual);
        outState.putBoolean(KEY_SESION_INICIADA, sesionIniciada);
        outState.putLong(KEY_SESSION_START_TIME, sessionStartTime);
        outState.putLong(KEY_REST_TIME, restTimeRemaining);
        outState.putBoolean(KEY_REST_PAUSED, restPaused);
    }

    @Override
    protected void onDestroy() {
        dismissRestDialog();
        super.onDestroy();
    }

    /**
     * Intercepta el botón de retroceso para mostrar un diálogo de confirmación
     * antes de salir del entrenamiento activo.
     */
    @Override
    public void onBackPressed() {
        mostrarDialogoSalir();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Inicialización
    // ═════════════════════════════════════════════════════════════════════

    /** Vincula todas las vistas del layout. */
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
        videoButton = findViewById(R.id.videoButton);

        rvSets.setLayoutManager(new LinearLayoutManager(this));
    }

    /** Configura los observers del ViewModel para reaccionar a estados de guardado. */
    private void observeViewModel() {
        // Estado del guardado de series
        viewModel.getSaveSerieState().observe(this, state -> {
            if (state == null) return;
            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                viewModel.clearSaveSerieState();
            }
        });

        // Evento de guardado exitoso → mostrar modal de descanso
        viewModel.getSaveSerieSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) return;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            showRestDialog();
        });
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Carga de ejercicios
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Carga el ejercicio en la posición {@link #indiceActual} y actualiza
     * todas las vistas asociadas: nombre, grupos musculares, progreso,
     * adaptador de series, y botones de navegación.
     */
    private void cargarEjercicio() {
        ejercicioActual = listaEjercicios.get(indiceActual);

        tvExerciseName.setText(ejercicioActual.getNombre());
        tvMuscles.setText(ejercicioActual.getGrupoMuscular());
        tvProgress.setText(getString(
                R.string.workout_exercise_progress,
                indiceActual + 1,
                listaEjercicios.size()));

        // Deshabilitar "Anterior" en el primer ejercicio
        btnPrevious.setEnabled(indiceActual > 0);

        // Cambiar texto del botón "Siguiente" al terminar todos los ejercicios
        if (indiceActual == listaEjercicios.size() - 1) {
            btnNext.setText(R.string.workout_finish_routine);
        } else {
            btnNext.setText(R.string.exercise_next);
        }

        // Crear el adaptador con las series del ejercicio actual,
        // vinculando cada serie al callback de guardado del ViewModel
        adapter = new SetAdapter(ejercicioActual.getSeries(), (serie, weight) ->
                viewModel.saveSerie(ejercicioActual.getNombre(), serie, weight));
        rvSets.setAdapter(adapter);

        actualizarEstadoCompletado();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Modal de descanso (CountDownTimer de 90s)
    // ═════════════════════════════════════════════════════════════════════

    /** Muestra el diálogo modal con el temporizador de descanso. */
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

        // Botón de pausa/reanudar
        btnPause.setOnClickListener(v -> {
            if (restPaused) {
                restPaused = false;
                btnPause.setText(R.string.rest_dialog_pause);
                iniciarTimerDialog(tvTimer, btnPause);
            } else {
                restPaused = true;
                btnPause.setText(R.string.rest_dialog_continue);
                if (restCountDownTimer != null) {
                    restCountDownTimer.cancel();
                }
            }
        });

        // Botón para saltar el descanso
        btnSkip.setOnClickListener(v -> dismissRestDialog());
    }

    /**
     * Inicia (o reinicia) el CountDownTimer con el tiempo restante
     * acumulado en {@link #restTimeRemaining}.
     */
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
                vibrar();
                dismissRestDialog();
            }
        }.start();
    }

    /** Cierra el modal de descanso y cancela el timer activo. */
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
        actualizarEstadoCompletado();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Indicador de ejercicio completado
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Muestra u oculta el indicador visual de "ejercicio completado"
     * según si todas las series del ejercicio actual están marcadas
     * como {@link Serie#isCompletada()}.
     */
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

    // ═════════════════════════════════════════════════════════════════════
    //  Vibración
    // ═════════════════════════════════════════════════════════════════════

    /** Vibra el dispositivo durante 400ms al terminar el descanso. */
    private void vibrar() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(400);
        }
    }
    private void mostrarVideoEjercicio(Ejercicio ejercicio) {
        String nombreVideo = ejercicio.getImagenUrl();
        if (nombreVideo == null || nombreVideo.isEmpty()) {
            Toast.makeText(this, "No hay video disponible para este ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        int videoResId = getResources().getIdentifier(nombreVideo, "raw", getPackageName());
        if (videoResId == 0) {
            Toast.makeText(this, "Video no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        android.net.Uri videoUri =
                android.net.Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);

        // Inflar la vista antes de crear el diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_video, null);
        android.widget.VideoView videoView = dialogView.findViewById(R.id.videoView);

        // Obtener dimensiones del video ANTES de mostrar el diálogo
        if (videoView != null) {
            int videoWidth = 0, videoHeight = 0;
            android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
            try {
                retriever.setDataSource(this, videoUri);
                String w = retriever.extractMetadata(
                        android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String h = retriever.extractMetadata(
                        android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                if (w != null && h != null) {
                    videoWidth = Integer.parseInt(w);
                    videoHeight = Integer.parseInt(h);
                }
            } catch (Exception e) {
                // Si falla, usar valores por defecto (16:9)
                videoWidth = 16;
                videoHeight = 9;
            } finally {
                retriever.release();
            }

            // Calcular altura respetando aspect ratio
            if (videoWidth > 0 && videoHeight > 0) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int screenWidth = metrics.widthPixels;
                int calculatedHeight = screenWidth * videoHeight / videoWidth;
                int minHeight = (int) (200 * metrics.density);
                int maxHeight = (int) (280 * metrics.density);
                int clampedHeight = Math.max(minHeight, Math.min(calculatedHeight, maxHeight));

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoView.getLayoutParams();
                params.height = clampedHeight;
                videoView.setLayoutParams(params);
            }

            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                videoView.start();
            });
        }

        // Consejos
        TextView tvConsejos = dialogView.findViewById(R.id.tvConsejos);
        if (tvConsejos != null) {
            String tips = ejercicio.getConsejos();
            tvConsejos.setText(tips != null ? tips : "");
            tvConsejos.setVisibility(tips != null && !tips.isEmpty() ? View.VISIBLE : View.GONE);
        }

        // Crear y mostrar el diálogo con la vista ya configurada
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle(ejercicio.getNombre())
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .create();
        dialog.show();
    }
    // ═════════════════════════════════════════════════════════════════════
    //  Navegación entre ejercicios
    // ═════════════════════════════════════════════════════════════════════

    /** Navega al ejercicio anterior, guardando el progreso del actual. */
    private void navegarAnterior() {
        guardarProgresoEjercicioActual();
        if (indiceActual > 0) {
            indiceActual--;
            cargarEjercicio();
        }
    }

    /**
     * Navega al siguiente ejercicio o finaliza la rutina.
     * <p>
     * Si alguna serie del ejercicio actual no está completada, muestra
     * un diálogo de confirmación preguntando si desea saltar las series
     * incompletas (comportamiento agregado tras feedback de usuario).
     * </p>
     */
    private void navegarSiguiente() {
        if (!todasLasSeriesCompletadas()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_skip_series_title)
                    .setMessage(R.string.confirm_skip_series_message)
                    .setPositiveButton(R.string.confirm_skip_series_yes, (dialog, which) -> {
                        guardarProgresoEjercicioActual();
                        avanzarAlSiguiente();
                    })
                    .setNegativeButton(R.string.confirm_skip_series_no, null)
                    .show();
            return;
        }
        guardarProgresoEjercicioActual();
        avanzarAlSiguiente();
    }

    /**
     * Verifica si todas las series del ejercicio actual están completadas.
     *
     * @return true si todas las series están completadas, false en caso contrario.
     */
    private boolean todasLasSeriesCompletadas() {
        if (ejercicioActual == null) return true;
        for (Serie s : ejercicioActual.getSeries()) {
            if (!s.isCompletada()) return false;
        }
        return true;
    }

    /**
     * Avanza al siguiente ejercicio o finaliza la sesión si es el último.
     * <p>
     * Al terminar la rutina, calcula la duración total, finaliza la sesión
     * en Firestore, y redirige a {@link WorkoutSummaryActivity}.
     * </p>
     */
    private void avanzarAlSiguiente() {
        if (indiceActual + 1 < listaEjercicios.size()) {
            indiceActual++;
            cargarEjercicio();
        } else {
            viewModel.finalizarSesion();
            long duracionMs = System.currentTimeMillis() - sessionStartTime;
            int duracionMin = (int) (duracionMs / 60000);

            Intent summaryIntent = new Intent(this, WorkoutSummaryActivity.class);
            summaryIntent.putExtra(WorkoutSummaryActivity.EXTRA_GRUPO_MUSCULAR, grupoMuscular);
            summaryIntent.putExtra(WorkoutSummaryActivity.EXTRA_TOTAL_EJERCICIOS, listaEjercicios.size());
            summaryIntent.putExtra(WorkoutSummaryActivity.EXTRA_DURACION_MINUTOS, duracionMin);
            startActivity(summaryIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
            finish();
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Salida y persistencia
    // ═════════════════════════════════════════════════════════════════════

    /** Muestra un diálogo de confirmación antes de salir del entrenamiento. */
    private void mostrarDialogoSalir() {
        dismissRestDialog();
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_exit_title)
                .setMessage(R.string.confirm_exit_message)
                .setPositiveButton(R.string.confirm_exit_yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.confirm_exit_no, null)
                .show();
    }

    /**
     * Guarda el progreso del ejercicio actual en Firestore si al menos
     * una serie fue completada.
     */
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
}
