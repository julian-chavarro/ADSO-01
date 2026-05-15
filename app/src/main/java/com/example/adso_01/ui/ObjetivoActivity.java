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

    String[] sexo = {"Masculino","Femenino"};
    String[] actividad = {"Ligera","Moderada","Intensa"};
    String[] objetivo = {"Ganar músculo","Perder grasa","Mantener peso"};

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

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        cargarSpinners();
        cargarDatosUsuario();

        btnCalcular.setOnClickListener(v -> calcularCalorias());
        btnVolver.setOnClickListener(v -> finish());
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
                android.R.layout.simple_spinner_item, objetivo));
    }

    private void calcularCalorias() {
        int edad = (int) spEdad.getSelectedItem();
        int peso = (int) spPeso.getSelectedItem();
        int estatura = (int) spEstatura.getSelectedItem();

        String sexoSel = spSexo.getSelectedItem().toString();
        String actividadSel = spActividad.getSelectedItem().toString();
        String objetivoSel = spObjetivo.getSelectedItem().toString();

        double calorias = calcularBMR(edad, peso, estatura, sexoSel);

        switch (actividadSel) {
            case "Ligera": calorias *= 1.3; break;
            case "Moderada": calorias *= 1.5; break;
            case "Intensa": calorias *= 1.7; break;
        }

        switch (objetivoSel) {
            case "Ganar músculo": calorias += 300; break;
            case "Perder grasa": calorias -= 300; break;
        }

        int caloriasFinal = (int) calorias;
        txtCalorias.setText(caloriasFinal + " kcal");

        Usuario usuario = new Usuario(
                edad, peso, estatura,
                sexoSel, actividadSel, objetivoSel,
                caloriasFinal
        );

        // REFACTORED: Ahora usamos el callback personalizado que devuelve (success, error)
        // en lugar de exponer el objeto Task de Firebase.
        viewModel.guardarUsuario(usuario, (success, error) -> {
            if (success) {
                Toast.makeText(this, "Objetivos guardados 🔥", Toast.LENGTH_SHORT).show();
            } else {
                String errorMsg = error != null ? error.getMessage() : "Error desconocido";
                Toast.makeText(this, "Error al guardar: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calcularBMR(int edad, int peso, int estatura, String sexo) {
        return sexo.equals("Masculino")
                ? (10 * peso) + (6.25 * estatura) - (5 * edad) + 5
                : (10 * peso) + (6.25 * estatura) - (5 * edad) - 161;
    }

    private void cargarDatosUsuario() {
        viewModel.obtenerUsuario((usuario, error) -> {
            if (error != null || usuario == null) {
                return;
            }

            spEdad.setSelection(Math.max(0, usuario.getEdad() - 10));
            spPeso.setSelection(Math.max(0, usuario.getPeso() - 30));
            spEstatura.setSelection(Math.max(0, usuario.getEstatura() - 120));

            int idxSexo = Arrays.asList(sexo).indexOf(usuario.getSexo());
            if (idxSexo >= 0) spSexo.setSelection(idxSexo);

            int idxActividad = Arrays.asList(actividad).indexOf(usuario.getActividad());
            if (idxActividad >= 0) spActividad.setSelection(idxActividad);

            int idxObjetivo = Arrays.asList(objetivo).indexOf(usuario.getObjetivo());
            if (idxObjetivo >= 0) spObjetivo.setSelection(idxObjetivo);

            txtCalorias.setText("Calorías recomendadas: " + usuario.getCalorias() + " kcal");
        });
    }
}
