package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.util.FitnessGoals;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.util.Navigator;
import com.example.adso_01.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Activity para la configuración de objetivos fitness del usuario.
 * <p>
 * Permite al usuario ingresar sus datos demográficos (edad, peso, estatura,
 * sexo, actividad física) y seleccionar un objetivo fitness. Calcula las
 * calorías diarias recomendadas usando la fórmula de Mifflin-St Jeor
 * y guarda el perfil completo en Firestore.
 * </p>
 */
@AndroidEntryPoint
public class ObjetivoActivity extends AppCompatActivity {

    /** Spinners para la entrada de datos del usuario. */
    Spinner spEdad, spPeso, spEstatura, spSexo, spActividad, spObjetivo;

    /** Botón para calcular y guardar las calorías. */
    Button btnCalcular;

    /** Texto que muestra las calorías calculadas. */
    TextView txtCalorias;

    /** Botón para volver al Lobby. */
    Button btnVolver;

    /** ViewModel para la gestión del perfil de usuario. */
    private UserViewModel viewModel;

    /** Opciones de sexo disponibles en el spinner. */
    private String[] sexo;

    /** Opciones de nivel de actividad física. */
    private String[] actividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivos);

        // Vincular vistas del layout
        spEdad = findViewById(R.id.spEdad);
        spPeso = findViewById(R.id.spPeso);
        spEstatura = findViewById(R.id.spEstatura);
        spSexo = findViewById(R.id.spSexo);
        spActividad = findViewById(R.id.spActividad);
        spObjetivo = findViewById(R.id.spObjetivo);
        btnCalcular = findViewById(R.id.btnCalcular);
        txtCalorias = findViewById(R.id.txtCalorias);
        btnVolver = findViewById(R.id.btnVolver);

        // Inicializar arrays con strings del recurso (necesario para i18n)
        sexo = new String[]{
                getString(R.string.objetivo_sexo_masculino),
                getString(R.string.objetivo_sexo_femenino)
        };
        actividad = new String[]{
                getString(R.string.objetivo_actividad_ligera),
                getString(R.string.objetivo_actividad_moderada),
                getString(R.string.objetivo_actividad_intensa)
        };

        // Inicializar ViewModel con Hilt
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Poblar los spinners con valores predeterminados
        cargarSpinners();
        observeViewModel();

        // Click listener para calcular y guardar
        btnCalcular.setOnClickListener(v -> calcularYGuardar());

        // Click listener para volver al lobby con animación
        btnVolver.setOnClickListener(v -> Navigator.finishWithBackAnim(this));

        // Cargar datos existentes del usuario si los hay
        viewModel.loadUser();
    }

    /**
     * Observa los estados del ViewModel para reaccionar a cambios
     * en la carga y guardado del perfil.
     */
    private void observeViewModel() {
        // Observar estado del perfil de usuario
        viewModel.getUserState().observe(this, state -> {
            if (state == null) return;

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearUserState();
                return;
            }

            if (!state.isSuccess()) return;

            Usuario usuario = state.getData();
            if (usuario == null) return;

            // Rellenar el formulario con los datos existentes
            aplicarUsuarioEnFormulario(usuario);
        });

        // Observar estado del guardado
        viewModel.getSaveState().observe(this, state -> {
            if (state == null) return;

            // Deshabilitar botón durante la carga
            btnCalcular.setEnabled(!state.isLoading());

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearSaveState();
            }
        });

        // Observar evento de guardado exitoso (un solo consumo)
        viewModel.getSaveSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) return;
            Toast.makeText(this, R.string.success_save_goals, Toast.LENGTH_SHORT).show();
        });
    }

    /** Puebla los spinners con los valores predeterminados para cada campo. */
    private void cargarSpinners() {
        spSexo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sexo));

        // Edades de 10 a 100 años
        Integer[] edades = IntStream.rangeClosed(10, 100).boxed().toArray(Integer[]::new);
        spEdad.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, edades));

        // Pesos de 30 a 200 kg
        Integer[] pesos = IntStream.rangeClosed(30, 200).boxed().toArray(Integer[]::new);
        spPeso.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pesos));

        // Estaturas de 120 a 220 cm
        Integer[] estaturas = IntStream.rangeClosed(120, 220).boxed().toArray(Integer[]::new);
        spEstatura.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estaturas));

        spActividad.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, actividad));

        spObjetivo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, FitnessGoals.OBJETIVOS));
    }

    /**
     * Calcula las calorías recomendadas según los datos del formulario
     * y guarda el perfil completo del usuario en Firestore.
     */
    private void calcularYGuardar() {
        int edad = (int) spEdad.getSelectedItem();
        int peso = (int) spPeso.getSelectedItem();
        int estatura = (int) spEstatura.getSelectedItem();

        String sexoSel = spSexo.getSelectedItem().toString();
        String actividadSel = spActividad.getSelectedItem().toString();
        String objetivoSel = spObjetivo.getSelectedItem().toString();

        // Calcular calorías usando la fórmula de Mifflin-St Jeor
        int calorias = viewModel.calcularCaloriasRecomendadas(
                edad, peso, estatura, sexoSel, actividadSel, objetivoSel);
        txtCalorias.setText(getString(R.string.objetivo_calorias_format, calorias));

        // Construir y guardar el objeto Usuario
        Usuario usuario = viewModel.buildUsuarioFromForm(
                edad, peso, estatura, sexoSel, actividadSel, objetivoSel);
        viewModel.saveUser(usuario);
    }

    /**
     * Rellena los spinners con los valores del usuario existente en Firestore.
     * Incluye migración legacy para el objetivo "Ganar músculo".
     *
     * @param usuario Objeto Usuario con los datos cargados.
     */
    private void aplicarUsuarioEnFormulario(Usuario usuario) {
        if (usuario.getEdad() > 0) {
            spEdad.setSelection(Math.max(0, usuario.getEdad() - 10));
        }
        if (usuario.getPeso() > 0) {
            spPeso.setSelection(Math.max(0, usuario.getPeso() - 30));
        }
        if (usuario.getEstatura() > 0) {
            spEstatura.setSelection(Math.max(0, usuario.getEstatura() - 120));
        }

        int idxSexo = Arrays.asList(sexo).indexOf(usuario.getSexo());
        if (idxSexo >= 0) {
            spSexo.setSelection(idxSexo);
        }

        int idxActividad = Arrays.asList(actividad).indexOf(usuario.getActividad());
        if (idxActividad >= 0) {
            spActividad.setSelection(idxActividad);
        }

        int idxObjetivo = Arrays.asList(FitnessGoals.OBJETIVOS).indexOf(usuario.getObjetivo());
        if (idxObjetivo >= 0) {
            spObjetivo.setSelection(idxObjetivo);
        } else if (FitnessGoals.GANAR_MUSCULO.equals(usuario.getObjetivo())) {
            // Migración legacy: "Ganar músculo" → "Ganar masa muscular"
            int idx = Arrays.asList(FitnessGoals.OBJETIVOS).indexOf(FitnessGoals.GANAR_MASA_MUSCULAR);
            if (idx >= 0) spObjetivo.setSelection(idx);
        }

        if (usuario.getCalorias() > 0) {
            txtCalorias.setText(getString(R.string.objetivo_calorias_format, usuario.getCalorias()));
        }
    }
}
