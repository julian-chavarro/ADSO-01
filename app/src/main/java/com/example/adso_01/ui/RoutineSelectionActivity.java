package com.example.adso_01.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.util.Navigator;
import com.example.adso_01.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Pantalla de selección de grupo muscular para el entrenamiento del día.
 * <p>
 * Presenta botones para cada grupo muscular. Para usuarios masculinos muestra
 * Pecho, Espalda, Pierna, Hombro, Full Body. Para usuarias femeninas muestra
 * Pierna, Glúteo + Abdomen, Tren Superior, Full Body y las rutinas se adaptan
 * según el objetivo fitness de la usuaria (Ganar masa muscular, Perder grasa,
 * Mantenimiento).
 * </p>
 */
@AndroidEntryPoint
public class RoutineSelectionActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private String sexoUsuario;
    private String objetivoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_selection);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        observeUser();
        userViewModel.loadUser();

        // Botón para volver al Lobby
        findViewById(R.id.btnBackSelection).setOnClickListener(v ->
                Navigator.finishWithBackAnim(this));
    }

    /**
     * Observa el perfil del usuario desde Firestore. Cuando los datos llegan,
     * configura los botones según el sexo del usuario.
     */
    private void observeUser() {
        userViewModel.getUserState().observe(this, state -> {
            if (state == null || !state.isSuccess()) return;
            Usuario user = state.getData();
            if (user == null) return;
            sexoUsuario = user.getSexo();
            objetivoUsuario = user.getObjetivo();
            configurarBotones();
        });
    }

    /**
     * Configura los botones de grupo muscular según el sexo del usuario.
     * <p>
     * Hombres: 5 grupos (Pecho, Espalda, Pierna, Hombro, Full Body).
     * Mujeres: 4 grupos (Pierna, Glúteo + Abdomen, Tren Superior, Full Body).
     * </p>
     */
    private void configurarBotones() {
        boolean esMujer = "Femenino".equals(sexoUsuario);

        if (esMujer) {
            setupMuscleGroupButton(R.id.btnDay1, "Pierna");
            setupMuscleGroupButton(R.id.btnDay2, "Glúteo + Abdomen");
            setupMuscleGroupButton(R.id.btnDay3, "Tren Superior");
            setupMuscleGroupButton(R.id.btnDay4, "Full Body");
            findViewById(R.id.btnDay5).setVisibility(View.GONE);
        } else {
            setupMuscleGroupButton(R.id.btnDay1, "Pecho");
            setupMuscleGroupButton(R.id.btnDay2, "Espalda");
            setupMuscleGroupButton(R.id.btnDay3, "Pierna");
            setupMuscleGroupButton(R.id.btnDay4, "Hombro");
            setupMuscleGroupButton(R.id.btnDay5, "Full Body");
        }
    }

    /**
     * Configura un botón del layout para que al presionarlo navegue
     * al calentamiento y luego al entrenamiento activo, pasando el grupo
     * muscular, sexo y objetivo del usuario.
     *
     * @param buttonId      ID del botón en el layout (R.id.btnDay1, etc.).
     * @param grupoMuscular Nombre clave del grupo muscular.
     */
    private void setupMuscleGroupButton(int buttonId, String grupoMuscular) {
        Button btn = findViewById(buttonId);
        if (btn != null) {
            btn.setOnClickListener(v ->
                    Navigator.toWarmup(this, grupoMuscular, sexoUsuario, objetivoUsuario));
        }
    }
}
