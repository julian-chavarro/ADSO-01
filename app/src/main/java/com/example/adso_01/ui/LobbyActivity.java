package com.example.adso_01.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.util.NutritionPreferences;
import com.example.adso_01.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Dashboard principal con:
 * - Objetivo EXACTO del usuario desde Firebase (sin normalización)
 * - Calorías recomendadas calculadas según perfil (Mifflin-St Jeor)
 * - Progreso nutricional diario (desayuno/almuerzo/cena) con fecha Colombia
 * - Reloj en español Colombia con zona horaria America/Bogota
 * - Validación de perfil: bloquea funciones si no hay objetivos configurados
 * - Logo ADSO Fitness en lugar de iniciales
 * - Bottom nav reemplazada: "Entrenar" en lugar de "Inicio"
 */
public class LobbyActivity extends AppCompatActivity {

    private TextView tvGreeting, tvRealDateTime;
    private ImageView imgLogo;
    private MaterialCardView cardObjetivoPrincipal, cardNutricionPrincipal, cardChat, cardProgreso;
    private TextView tvObjetivoValue, tvCaloriasValue;
    private TextView tvNutricionProgreso, tvProgresoSub, tvMotivacionExtra;
    private ProgressBar progressNutricion;
    private MaterialButton btnCtaEntrenar;

    private UserViewModel userViewModel;
    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    // Locale Colombia para fecha/hora
    private static final Locale LOCALE_CO = new Locale("es", "CO");
    private static final TimeZone TZ_BOGOTA = TimeZone.getTimeZone("America/Bogota");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initViews();
        setupViewModels();
        setupClickListeners();
        setupBottomBar();
        startClock();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvRealDateTime = findViewById(R.id.tvRealDateTime);
        imgLogo = findViewById(R.id.imgLogo);

        cardObjetivoPrincipal = findViewById(R.id.cardObjetivoPrincipal);
        tvObjetivoValue = findViewById(R.id.tvObjetivoValue);
        tvCaloriasValue = findViewById(R.id.tvCaloriasValue);

        cardNutricionPrincipal = findViewById(R.id.cardNutricionPrincipal);
        progressNutricion = findViewById(R.id.progressNutricion);
        tvNutricionProgreso = findViewById(R.id.tvNutricionProgreso);

        cardChat = findViewById(R.id.cardChat);
        cardProgreso = findViewById(R.id.cardProgreso);
        tvProgresoSub = findViewById(R.id.tvProgresoSub);

        tvMotivacionExtra = findViewById(R.id.tvMotivacionExtra);

        btnCtaEntrenar = findViewById(R.id.btnCtaEntrenar);
    }

    private void setupViewModels() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUserState().observe(this, resource -> {
            if (resource == null) return;
            if (resource.isSuccess() && resource.getData() != null) {
                updateUIWithUserData(resource.getData());
            }
        });

        userViewModel.loadUser();
    }

    private void updateUIWithUserData(Usuario usuario) {
        // ── Saludo (PROBLEMA 3: "¡Vamos con todo, nombre!") ──
        if (usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
            tvGreeting.setText("¡Vamos con todo, " + usuario.getNombre() + "!");
        } else {
            tvGreeting.setText("¡Vamos con todo!");
        }

        // PROBLEMA 4: Logo — se usa ImageView con drawable, no necesita lógica Java

        // ── Determinar si el usuario tiene perfil completo ──
        boolean tieneObjetivos = usuario.getCalorias() > 0
                && usuario.getObjetivo() != null
                && !usuario.getObjetivo().isEmpty();

        // ── Card: Objetivo Principal (PROBLEMA 1: valor exacto desde Firebase) ──
        if (tieneObjetivos) {
            // Mostrar el objetivo EXACTO que el usuario seleccionó, sin normalizar
            tvObjetivoValue.setText(usuario.getObjetivo());

            // Formatear calorías con separador de miles (es_CO → puntos)
            NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_CO);
            String caloriasStr = nf.format(usuario.getCalorias());
            tvCaloriasValue.setText(getString(R.string.lobby_calories_value, caloriasStr));

            cardObjetivoPrincipal.setVisibility(View.VISIBLE);
        } else {
            tvObjetivoValue.setText(R.string.lobby_no_goals_title);
            tvCaloriasValue.setText("-- kcal/día");
            cardObjetivoPrincipal.setVisibility(View.VISIBLE);
        }

        // ── Card: Nutrición con progreso real y fecha Colombia (PROBLEMA 6) ──
        actualizarProgresoNutricion();

    }

    /**
     * Lee el estado real de las comidas desde SharedPreferences (con fecha Colombia)
     * y actualiza la barra de progreso nutricional.
     */
    private void actualizarProgresoNutricion() {
        int progreso = NutritionPreferences.getProgresoHoy(this);
        progressNutricion.setProgress(progreso);
        tvNutricionProgreso.setText(getString(R.string.lobby_nutrition_progress, progreso));
    }

    private void setupClickListeners() {
        cardObjetivoPrincipal.setOnClickListener(v ->
                startActivity(new Intent(this, ObjetivoActivity.class)));

        cardNutricionPrincipal.setOnClickListener(v -> {
            if (!tienePerfilCompleto()) {
                mostrarMensajeNoGoals();
                return;
            }
            startActivity(new Intent(this, NutritionActivity.class));
        });

        cardChat.setOnClickListener(v ->
                startActivity(new Intent(this, ChatActivity.class)));

        cardProgreso.setOnClickListener(v -> {
            if (!tienePerfilCompleto()) {
                mostrarMensajeNoGoals();
                return;
            }
            startActivity(new Intent(this, ProgressActivity.class));
        });
    }

    private void setupBottomBar() {
        // CTA: EMPEZAR ENTRENAMIENTO → RoutineSelectionActivity
        btnCtaEntrenar.setOnClickListener(v -> {
            if (!tienePerfilCompleto()) {
                mostrarMensajeNoGoals();
                return;
            }
            startActivity(new Intent(this, RoutineSelectionActivity.class));
        });
    }

    // ─── Validación de perfil ───────────────────────────────────────────

    private boolean tienePerfilCompleto() {
        if (userViewModel == null) return false;
        if (userViewModel.getUserState().getValue() == null) return false;
        if (!userViewModel.getUserState().getValue().isSuccess()) return false;
        Usuario usuario = userViewModel.getUserState().getValue().getData();
        if (usuario == null) return false;
        return usuario.getCalorias() > 0
                && usuario.getObjetivo() != null
                && !usuario.getObjetivo().isEmpty();
    }

    private void mostrarMensajeNoGoals() {
        Toast.makeText(this,
                R.string.lobby_no_goals_message,
                Toast.LENGTH_LONG).show();
    }

    // ─── Reloj en español Colombia (PROBLEMA 5) ──────────────────────────

    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                // Formato: "Lunes, 30 de mayo de 2026 · 08:35 a. m."
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy · hh:mm a", LOCALE_CO);
                sdf.setTimeZone(TZ_BOGOTA);
                String currentDateTime = sdf.format(new Date());
                // Capitalizar primera letra del día
                if (currentDateTime.length() > 0) {
                    currentDateTime = currentDateTime.substring(0, 1).toUpperCase(LOCALE_CO)
                            + currentDateTime.substring(1);
                }
                tvRealDateTime.setText(currentDateTime);
                timeHandler.postDelayed(this, 60000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userViewModel.loadUser();
        actualizarProgresoNutricion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }
}
