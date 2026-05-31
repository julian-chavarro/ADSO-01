package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.util.NutritionPreferences;
import com.example.adso_01.viewmodel.UserViewModel;

/**
 * Actividad encargada de mostrar el plan nutricional dinámico basado en las calorías.
 * Sigue el patrón MVVM y la modularización de lógica de negocio.
 */
public class NutritionActivity extends AppCompatActivity {

    private TextView txtObjetivo, txtCalorias, txtDesayuno, txtAlmuerzo, txtCena, txtProgreso;
    private Button btnDesayuno, btnAlmuerzo, btnCena;
    private ImageButton btnVolver;

    private UserViewModel userViewModel;

    private int progreso = 0;
    private boolean desayunoCompleto = false, almuerzoCompleto = false, cenaCompleta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        initViews();
        setupViewModel();
        setupListeners();

        // Carga inicial de datos desde Firestore
        userViewModel.loadUser();
    }

    private void initViews() {
        txtObjetivo = findViewById(R.id.txtObjetivo);
        txtCalorias = findViewById(R.id.txtCalorias);
        txtDesayuno = findViewById(R.id.txtDesayuno);
        txtAlmuerzo = findViewById(R.id.txtAlmuerzo);
        txtCena = findViewById(R.id.txtCena);
        txtProgreso = findViewById(R.id.txtProgreso);
        btnDesayuno = findViewById(R.id.btnDesayuno);
        btnAlmuerzo = findViewById(R.id.btnAlmuerzo);
        btnCena = findViewById(R.id.btnCena);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void setupViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserState().observe(this, resource -> {
            if (resource == null) return;

            if (resource.isSuccess() && resource.getData() != null) {
                Usuario usuario = resource.getData();
                if (usuario.getCalorias() > 0) {
                    txtObjetivo.setText(getString(R.string.nutrition_objetivo_format, usuario.getObjetivo()));
                    txtCalorias.setText(getString(R.string.nutrition_calorias_format, usuario.getCalorias()));
                    generarDieta(usuario.getCalorias());
                } else {
                    Toast.makeText(this, R.string.error_no_goals, Toast.LENGTH_LONG).show();
                }
            } else if (resource.isError()) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());
        btnDesayuno.setOnClickListener(v -> marcarComida("desayuno"));
        btnAlmuerzo.setOnClickListener(v -> marcarComida("almuerzo"));
        btnCena.setOnClickListener(v -> marcarComida("cena"));
    }

    private void marcarComida(String tipo) {
        if (tipo.equals("desayuno") && !desayunoCompleto) {
            desayunoCompleto = true;
            btnDesayuno.setText("✅");
            btnDesayuno.setEnabled(false);
            progreso += 33;
            NutritionPreferences.marcarComida(this, "desayuno");
        } else if (tipo.equals("almuerzo") && !almuerzoCompleto) {
            almuerzoCompleto = true;
            btnAlmuerzo.setText("✅");
            btnAlmuerzo.setEnabled(false);
            progreso += 33;
            NutritionPreferences.marcarComida(this, "almuerzo");
        } else if (tipo.equals("cena") && !cenaCompleta) {
            cenaCompleta = true;
            btnCena.setText("✅");
            btnCena.setEnabled(false);
            progreso += 34;
            NutritionPreferences.marcarComida(this, "cena");
        }
        actualizarProgresoUI();
    }

    private void actualizarProgresoUI() {
        txtProgreso.setText("Progreso del día: " + progreso + "%");
        if (progreso >= 100) {
            txtProgreso.setText("✅ ¡Meta nutricional cumplida!");
        }
    }

    private void generarDieta(int calorias) {
        if (calorias >= 1800 && calorias <= 2000) {
            setPlanTexts(
                "3 huevos + 1 arepa (100g)\nAvena 60g + banano\nYogur griego 200g",
                "150g pollo + 150g arroz + ensalada + aguacate 50g",
                "150g pescado + verduras + papa 150g"
            );
        } else if (calorias <= 2200) {
            setPlanTexts(
                "4 huevos + 2 arepas (120g)\nAvena 70g + banano + mantequilla maní",
                "180g pollo + 180g arroz + ensalada + aguacate 60g",
                "180g pescado + verduras + papa 200g"
            );
        } else if (calorias <= 2400) {
            setPlanTexts(
                "5 huevos + 2 arepas (120g)\nAvena 80g + mantequilla maní 20g",
                "200g pollo + 200g arroz + ensalada + aguacate 70g",
                "200g pescado + verduras + papa 250g"
            );
        } else if (calorias <= 2800) {
            setPlanTexts(
                "6 huevos + 2 arepas (150g)\nAvena 100g + mantequilla maní 30g",
                "240g pollo + 240g arroz + ensalada + aguacate 90g",
                "240g pescado + verduras + papa 350g"
            );
        } else if (calorias <= 3200) {
            setPlanTexts(
                "6 huevos + 3 arepas (180g)\nAvena 120g + mantequilla maní 35g",
                "260g pollo + 260g arroz + ensalada + aguacate 100g",
                "260g pescado + verduras + papa 400g"
            );
        } else if (calorias <= 3600) {
            setPlanTexts(
                "8 huevos + 3 arepas (220g)\nAvena 140g + mantequilla maní 45g",
                "300g pollo + 300g arroz + ensalada + aguacate 120g",
                "300g pescado + verduras + papa 500g"
            );
        } else {
            setPlanTexts(
                "9 huevos + 4 arepas (260g)\nAvena 160g + mantequilla maní 60g",
                "350g pollo + 350g arroz + ensalada + aguacate 140g",
                "350g pescado + verduras + papa 600g"
            );
        }
    }

    private void setPlanTexts(String des, String alm, String cen) {
        txtDesayuno.setText(des);
        txtAlmuerzo.setText(alm);
        txtCena.setText(cen);
    }
}
