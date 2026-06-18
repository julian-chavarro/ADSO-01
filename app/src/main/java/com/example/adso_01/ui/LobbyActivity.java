package com.example.adso_01.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.util.Navigator;
import com.example.adso_01.util.WorkoutStreakManager;
import com.example.adso_01.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Dashboard principal de la aplicación.
 * <p>
 * Actúa como centro de navegación después del inicio de sesión.
 * Muestra el objetivo fitness del usuario, calorías recomendadas,
 * una tarjeta de nutrición, enlaces a ChatFit y Progreso, una
 * sección de motivación, y un botón CTA "Empezar entrenamiento"
 * en la barra inferior. También gestiona:
 * <ul>
 *   <li>Saludo personalizado con el nombre del usuario.</li>
 *   <li>Racha de entrenamientos consecutivos (streak).</li>
 *   <li>Frase motivacional aleatoria.</li>
 *   <li>Reloj en español con zona horaria Colombia.</li>
 *   <li>Validación de perfil: bloquea funciones si no hay objetivos.</li>
 * </ul>
 * </p>
 */
@AndroidEntryPoint
public class LobbyActivity extends AppCompatActivity {

    // ─── Vistas ──────────────────────────────────────────────────────────

    private TextView tvGreeting, tvRealDateTime;
    private ImageView imgLogo;
    private MaterialCardView cardObjetivoPrincipal, cardNutricionPrincipal, cardChat, cardProgreso;
    private TextView tvObjetivoValue, tvCaloriasValue;
    private TextView tvProgresoSub, tvMotivacionExtra;
    private MaterialButton btnCtaEntrenar;
    private TextView tvFraseMotivacional, tvStreak;

    // ─── Dependencias ────────────────────────────────────────────────────

    private UserViewModel userViewModel;
    private WorkoutStreakManager streakManager;

    /** Handler para el reloj en tiempo real. */
    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    /** Locale colombiano para formato de números y fechas. */
    private static final Locale LOCALE_CO = new Locale("es", "CO");

    /** Zona horaria Colombia. */
    private static final TimeZone TZ_BOGOTA = TimeZone.getTimeZone("America/Bogota");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initViews();
        streakManager = new WorkoutStreakManager(this);
        actualizarStreak();
        actualizarFraseMotivacional();
        setupViewModels();
        setupClickListeners();
        setupBottomBar();
        startClock();
    }

    /** Vincula todas las vistas del layout. */
    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvRealDateTime = findViewById(R.id.tvRealDateTime);
        imgLogo = findViewById(R.id.imgLogo);

        cardObjetivoPrincipal = findViewById(R.id.cardObjetivoPrincipal);
        tvObjetivoValue = findViewById(R.id.tvObjetivoValue);
        tvCaloriasValue = findViewById(R.id.tvCaloriasValue);

        cardNutricionPrincipal = findViewById(R.id.cardNutricionPrincipal);
        cardChat = findViewById(R.id.cardChat);
        cardProgreso = findViewById(R.id.cardProgreso);
        tvProgresoSub = findViewById(R.id.tvProgresoSub);

        tvMotivacionExtra = findViewById(R.id.tvMotivacionExtra);
        tvFraseMotivacional = findViewById(R.id.tvFraseMotivacional);
        tvStreak = findViewById(R.id.tvStreak);

        btnCtaEntrenar = findViewById(R.id.btnCtaEntrenar);
    }

    /** Configura el ViewModel y observa los cambios del perfil del usuario. */
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

    /**
     * Actualiza la interfaz con los datos del usuario cargados desde Firestore.
     *
     * @param usuario Perfil del usuario con objetivos y calorías.
     */
    private void updateUIWithUserData(Usuario usuario) {
        // Saludo personalizado con el nombre del usuario
        if (usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
            tvGreeting.setText("¡Vamos con todo, " + usuario.getNombre() + "!");
        } else {
            tvGreeting.setText("¡Vamos con todo!");
        }

        // Verifica si el usuario tiene un perfil completo con objetivos
        boolean tieneObjetivos = usuario.getCalorias() > 0
                && usuario.getObjetivo() != null
                && !usuario.getObjetivo().isEmpty();

        // Actualiza la tarjeta de Objetivo Principal
        if (tieneObjetivos) {
            tvObjetivoValue.setText(usuario.getObjetivo());
            NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_CO);
            String caloriasStr = nf.format(usuario.getCalorias());
            tvCaloriasValue.setText(getString(R.string.lobby_calories_value, caloriasStr));
            cardObjetivoPrincipal.setVisibility(View.VISIBLE);
        } else {
            tvObjetivoValue.setText(R.string.lobby_no_goals_title);
            tvCaloriasValue.setText("-- kcal/día");
            cardObjetivoPrincipal.setVisibility(View.VISIBLE);
        }
    }

    /** Configura los click listeners de las cards del Lobby. */
    private void setupClickListeners() {
        cardObjetivoPrincipal.setOnClickListener(v -> Navigator.toObjetivo(this));

        cardNutricionPrincipal.setOnClickListener(v -> {
            if (!tienePerfilCompleto()) {
                mostrarMensajeNoGoals();
                return;
            }
            Navigator.toNutrition(this);
        });

        cardChat.setOnClickListener(v -> Navigator.toChat(this));

        cardProgreso.setOnClickListener(v -> {
            if (!tienePerfilCompleto()) {
                mostrarMensajeNoGoals();
                return;
            }
            Navigator.toProgress(this);
        });
    }

    /** Configura el botón CTA de la barra inferior para iniciar entrenamiento. */
    private void setupBottomBar() {
        btnCtaEntrenar.setOnClickListener(v -> {
            if (!tienePerfilCompleto()) {
                mostrarMensajeNoGoals();
                return;
            }
            Navigator.toRoutineSelection(this);
        });
    }

    // ─── Validación de perfil ─────────────────────────────────────────────

    /**
     * Verifica si el usuario tiene un perfil completo con objetivos y calorías configurados.
     *
     * @return true si el perfil está completo, false si falta configuración.
     */
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

    /** Muestra un mensaje indicando que el usuario debe configurar sus objetivos primero. */
    private void mostrarMensajeNoGoals() {
        Toast.makeText(this,
                R.string.lobby_no_goals_message,
                Toast.LENGTH_LONG).show();
    }

    // ─── Streak y frase motivacional ──────────────────────────────────────

    /** Actualiza el texto de la racha de entrenamientos consecutivos. */
    private void actualizarStreak() {
        if (tvStreak == null) return;
        int streak = streakManager.getStreak();
        String text;
        if (streak == 0) {
            text = getString(R.string.lobby_streak_0);
        } else if (streak >= 7) {
            text = getString(R.string.lobby_streak_7, streak);
        } else if (streak == 1) {
            text = getString(R.string.lobby_streak_1, streak);
        } else {
            text = getString(R.string.lobby_streak_n, streak);
        }
        tvStreak.setText(text);
    }

    /** Selecciona y muestra una frase motivacional aleatoria del array de recursos. */
    private void actualizarFraseMotivacional() {
        if (tvFraseMotivacional == null) return;
        String[] frases = getResources().getStringArray(R.array.motivation_phrases_lobby);
        int index = new java.util.Random().nextInt(frases.length);
        tvFraseMotivacional.setText(frases[index]);
    }

    // ─── Reloj en español Colombia ────────────────────────────────────────

    /** Inicia el reloj que muestra la fecha y hora actual en zona Colombia. */
    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy · hh:mm a", LOCALE_CO);
                sdf.setTimeZone(TZ_BOGOTA);
                String currentDateTime = sdf.format(new Date());
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
        actualizarStreak();
        actualizarFraseMotivacional();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }
}
