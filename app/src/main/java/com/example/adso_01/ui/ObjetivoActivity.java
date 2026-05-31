package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.model.FitnessGoals;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ObjetivoActivity extends AppCompatActivity {

    Spinner spEdad, spPeso, spEstatura, spSexo, spActividad, spObjetivo;
    Button btnCalcular;
    TextView txtCalorias;
    ImageButton btnVolver;

    private UserViewModel viewModel;

    private final String[] sexo = {"Masculino", "Femenino"};
    private final String[] actividad = {"Ligera", "Moderada", "Intensa"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivos);

        spEdad = findViewById(R.id.spEdad);
        spPeso = findViewById(R.id.spPeso);
        spEstatura = findViewById(R.id.spEstatura);
        spSexo = findViewById(R.id.spSexo);
        spActividad = findViewById(R.id.spActividad);
        spObjetivo = findViewById(R.id.spObjetivo);
        btnCalcular = findViewById(R.id.btnCalcular);
        txtCalorias = findViewById(R.id.txtCalorias);
        btnVolver = findViewById(R.id.btnVolver);

        viewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(UserViewModel.class);

        cargarSpinners();
        observeViewModel();

        btnCalcular.setOnClickListener(v -> calcularYGuardar());
        btnVolver.setOnClickListener(v -> finish());

        viewModel.loadUser();
    }

    private void observeViewModel() {
        viewModel.getUserState().observe(this, state -> {
            if (state == null) {
                return;
            }

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearUserState();
                return;
            }

            if (!state.isSuccess()) {
                return;
            }

            Usuario usuario = state.getData();
            if (usuario == null) {
                return;
            }
            aplicarUsuarioEnFormulario(usuario);
        });

        viewModel.getSaveState().observe(this, state -> {
            if (state == null) {
                return;
            }
            btnCalcular.setEnabled(!state.isLoading());

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearSaveState();
            }
        });

        viewModel.getSaveSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) {
                return;
            }
            Toast.makeText(this, R.string.success_save_goals, Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarSpinners() {
        spSexo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sexo));

        Integer[] edades = IntStream.rangeClosed(10, 100).boxed().toArray(Integer[]::new);
        spEdad.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, edades));

        Integer[] pesos = IntStream.rangeClosed(30, 200).boxed().toArray(Integer[]::new);
        spPeso.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pesos));

        Integer[] estaturas = IntStream.rangeClosed(120, 220).boxed().toArray(Integer[]::new);
        spEstatura.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estaturas));

        spActividad.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, actividad));

        spObjetivo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, FitnessGoals.OBJETIVOS));
    }

    private void calcularYGuardar() {
        int edad = (int) spEdad.getSelectedItem();
        int peso = (int) spPeso.getSelectedItem();
        int estatura = (int) spEstatura.getSelectedItem();

        String sexoSel = spSexo.getSelectedItem().toString();
        String actividadSel = spActividad.getSelectedItem().toString();
        String objetivoSel = spObjetivo.getSelectedItem().toString();

        int calorias = viewModel.calcularCaloriasRecomendadas(
                edad, peso, estatura, sexoSel, actividadSel, objetivoSel);
        txtCalorias.setText(calorias + " kcal");

        Usuario usuario = viewModel.buildUsuarioFromForm(
                edad, peso, estatura, sexoSel, actividadSel, objetivoSel);
        viewModel.saveUser(usuario);
    }

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
            // Legacy: "Ganar músculo" → seleccionar "Ganar masa muscular"
            int idx = Arrays.asList(FitnessGoals.OBJETIVOS).indexOf(FitnessGoals.GANAR_MASA_MUSCULAR);
            if (idx >= 0) spObjetivo.setSelection(idx);
        }

        if (usuario.getCalorias() > 0) {
            txtCalorias.setText(getString(R.string.objetivo_calorias_format, usuario.getCalorias()));
        }
    }
}
