package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.viewmodel.UserViewModel;

public class NutritionActivity extends AppCompatActivity {

    TextView txtObjetivo, txtCalorias, txtDesayuno, txtAlmuerzo, txtCena, txtProgreso;
    Button btnDesayuno, btnAlmuerzo, btnCena;
    ImageButton btnVolver;

    private UserViewModel userViewModel;

    int progreso = 0;
    boolean desayunoCompleto = false;
    boolean almuerzoCompleto = false;
    boolean cenaCompleta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

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

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        cargarDatos();

        btnVolver.setOnClickListener(v -> finish());

        btnDesayuno.setOnClickListener(v -> {
            if (!desayunoCompleto) {
                desayunoCompleto = true;
                btnDesayuno.setText("✅ Completado");
                btnDesayuno.setEnabled(false);
                progreso += 33;
                actualizarProgreso();
            }
        });

        btnAlmuerzo.setOnClickListener(v -> {
            if (!almuerzoCompleto) {
                almuerzoCompleto = true;
                btnAlmuerzo.setText("✅ Completado");
                btnAlmuerzo.setEnabled(false);
                progreso += 33;
                actualizarProgreso();
            }
        });

        btnCena.setOnClickListener(v -> {
            if (!cenaCompleta) {
                cenaCompleta = true;
                btnCena.setText("✅ Completado");
                btnCena.setEnabled(false);
                progreso += 34;
                actualizarProgreso();
            }
        });
    }

    private void actualizarProgreso() {
        txtProgreso.setText("Progreso del día: " + progreso + "%");

        if (progreso == 100) {
            txtProgreso.setText("✅ Has completado el 100% de tu alimentación diaria");
        }
    }

    private void cargarDatos() {
        userViewModel.obtenerUsuario((usuario, error) -> {
            if (usuario == null) {
                return;
            }
            txtObjetivo.setText("Objetivo: " + usuario.getObjetivo());
            txtCalorias.setText("Calorías: " + usuario.getCalorias() + " kcal");
            generarDieta(usuario.getCalorias());
        });
    }

    private void generarDieta(int calorias) {

        if (calorias >= 1800 && calorias <= 2000) {

            txtDesayuno.setText(
                    "Opción 1: 3 huevos + 1 arepa (100g)\n" +
                            "Opción 2: Avena 60g + banano\n" +
                            "Opción 3: Pan integral 80g + 2 huevos + aguacate 40g\n" +
                            "Opción 4: Yogur griego 200g + avena 50g + frutos rojos\n" +
                            "Opción 5: Arepa 80g + 2 huevos + queso 40g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 150g pollo + 150g arroz + ensalada + aguacate 50g\n" +
                            "Opción 2: 150g carne magra + 130g arroz + plátano 100g\n" +
                            "Opción 3: 150g pollo + pasta integral 150g + verduras\n" +
                            "Opción 4: 150g carne + 150g arroz + frijoles 80g\n" +
                            "Opción 5: 150g pollo + 200g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 150g pescado + verduras + papa 150g\n" +
                            "Opción 2: 120g carne magra + 200g papa + ensalada\n" +
                            "Opción 3: 130g pollo + batata 200g + verduras\n" +
                            "Opción 4: Atún 140g + 200g papa + aguacate 50g\n" +
                            "Opción 5: 120g pollo + ensalada + aceite oliva 10g + pan integral 50g"
            );

        } else if (calorias <= 2200) {

            txtDesayuno.setText(
                    "Opción 1: 4 huevos + 2 arepas (120g)\n" +
                            "Opción 2: Avena 70g + banano + mantequilla de maní 15g\n" +
                            "Opción 3: Pan integral 100g + 3 huevos + aguacate 50g\n" +
                            "Opción 4: Yogur griego 250g + avena 60g + frutos rojos + nueces 15g\n" +
                            "Opción 5: Arepa 100g + 3 huevos + queso 50g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 180g pollo + 180g arroz + ensalada + aguacate 60g\n" +
                            "Opción 2: 180g carne magra + 150g arroz + plátano 120g\n" +
                            "Opción 3: 180g pollo + pasta integral 180g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 180g carne + 180g arroz + lentejas 100g\n" +
                            "Opción 5: 180g pollo + 250g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 180g pescado + verduras + papa 200g\n" +
                            "Opción 2: 150g carne magra + 250g papa + ensalada\n" +
                            "Opción 3: 160g pollo + batata 250g + verduras\n" +
                            "Opción 4: Atún 160g + 200g papa + aguacate 60g\n" +
                            "Opción 5: 150g pollo + ensalada + aceite oliva 10g + pan integral 70g"
            );

        }   else if (calorias <= 2400) {

                txtDesayuno.setText(
                        "Opción 1: 5 huevos + 2 arepas (120g)\n" +
                                "Opción 2: Avena 80g + banano + mantequilla de maní 20g\n" +
                                "Opción 3: Pan integral 120g + 3 huevos + aguacate 60g\n" +
                                "Opción 4: Yogur griego 300g + avena 70g + frutos rojos + nueces 20g\n" +
                                "Opción 5: Arepa 120g + 3 huevos + queso 60g"
                );

                txtAlmuerzo.setText(
                        "Opción 1: 200g pollo + 200g arroz + ensalada + aguacate 70g\n" +
                                "Opción 2: 200g carne magra + 180g arroz + plátano 150g\n" +
                                "Opción 3: 200g pollo + pasta integral 200g + verduras + aceite oliva 10g\n" +
                                "Opción 4: 200g carne + 200g arroz + lentejas 120g\n" +
                                "Opción 5: 200g pollo + 300g papa + ensalada + aceite oliva 10g"
                );

                txtCena.setText(
                        "Opción 1: 200g pescado + verduras + papa 250g\n" +
                                "Opción 2: 180g carne magra + 300g papa + ensalada\n" +
                                "Opción 3: 180g pollo + batata 300g + verduras\n" +
                                "Opción 4: Atún 180g + 250g papa + aguacate 70g\n" +
                                "Opción 5: 180g pollo + ensalada + aceite oliva 10g + pan integral 80g"
                );


        } else if (calorias <= 2600) {

            txtDesayuno.setText(
                    "Opción 1: 5 huevos + 2 arepas (140g)\n" +
                            "Opción 2: Avena 90g + banano + mantequilla de maní 25g\n" +
                            "Opción 3: Pan integral 140g + 4 huevos + aguacate 70g\n" +
                            "Opción 4: Yogur griego 350g + avena 80g + frutos rojos + nueces 25g\n" +
                            "Opción 5: Arepa 140g + 4 huevos + queso 70g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 220g pollo + 220g arroz + ensalada + aguacate 80g\n" +
                            "Opción 2: 220g carne magra + 200g arroz + plátano 180g\n" +
                            "Opción 3: 220g pollo + pasta integral 220g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 220g carne + 220g arroz + lentejas 140g\n" +
                            "Opción 5: 220g pollo + 350g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 220g pescado + verduras + papa 300g\n" +
                            "Opción 2: 200g carne magra + 350g papa + ensalada\n" +
                            "Opción 3: 200g pollo + batata 350g + verduras\n" +
                            "Opción 4: Atún 200g + 300g papa + aguacate 80g\n" +
                            "Opción 5: 200g pollo + ensalada + aceite oliva 10g + pan integral 100g"
            );
        } else if (calorias <= 2800) {

            txtDesayuno.setText(
                    "Opción 1: 6 huevos + 2 arepas (150g)\n" +
                            "Opción 2: Avena 100g + banano + mantequilla de maní 30g\n" +
                            "Opción 3: Pan integral 160g + 4 huevos + aguacate 80g\n" +
                            "Opción 4: Yogur griego 400g + avena 90g + frutos rojos + nueces 30g\n" +
                            "Opción 5: Arepa 150g + 4 huevos + queso 80g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 240g pollo + 240g arroz + ensalada + aguacate 90g\n" +
                            "Opción 2: 240g carne magra + 220g arroz + plátano 200g\n" +
                            "Opción 3: 240g pollo + pasta integral 240g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 240g carne + 240g arroz + lentejas 160g\n" +
                            "Opción 5: 240g pollo + 400g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 240g pescado + verduras + papa 350g\n" +
                            "Opción 2: 220g carne magra + 400g papa + ensalada\n" +
                            "Opción 3: 220g pollo + batata 400g + verduras\n" +
                            "Opción 4: Atún 220g + 350g papa + aguacate 90g\n" +
                            "Opción 5: 220g pollo + ensalada + aceite oliva 10g + pan integral 120g"
            );

        } else if (calorias <= 3000) {

            txtDesayuno.setText(
                    "Opción 1: 6 huevos + 3 arepas (170g)\n" +
                            "Opción 2: Avena 110g + banano + mantequilla de maní 30g\n" +
                            "Opción 3: Pan integral 170g + 4 huevos + aguacate 90g\n" +
                            "Opción 4: Yogur griego 420g + avena 95g + frutos rojos + nueces 30g\n" +
                            "Opción 5: Arepa 170g + 4 huevos + queso 90g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 250g pollo + 250g arroz + ensalada + aguacate 90g\n" +
                            "Opción 2: 250g carne magra + 230g arroz + plátano 200g\n" +
                            "Opción 3: 250g pollo + pasta integral 250g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 250g carne + 250g arroz + lentejas 160g\n" +
                            "Opción 5: 250g pollo + 400g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 250g pescado + verduras + papa 320g\n" +
                            "Opción 2: 230g carne magra + 380g papa + ensalada\n" +
                            "Opción 3: 230g pollo + batata 380g + verduras\n" +
                            "Opción 4: Atún 230g + 320g papa + aguacate 90g\n" +
                            "Opción 5: 230g pollo + ensalada + aceite oliva 10g + pan integral 120g"
            );

        }else if (calorias <= 3200) {

            txtDesayuno.setText(
                    "Opción 1: 6 huevos + 3 arepas (180g)\n" +
                            "Opción 2: Avena 120g + banano + mantequilla de maní 35g\n" +
                            "Opción 3: Pan integral 180g + 5 huevos + aguacate 90g\n" +
                            "Opción 4: Yogur griego 450g + avena 100g + frutos rojos + nueces 35g\n" +
                            "Opción 5: Arepa 180g + 5 huevos + queso 90g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 260g pollo + 260g arroz + ensalada + aguacate 100g\n" +
                            "Opción 2: 260g carne magra + 240g arroz + plátano 220g\n" +
                            "Opción 3: 260g pollo + pasta integral 260g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 260g carne + 260g arroz + lentejas 180g\n" +
                            "Opción 5: 260g pollo + 450g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 260g pescado + verduras + papa 400g\n" +
                            "Opción 2: 240g carne magra + 450g papa + ensalada\n" +
                            "Opción 3: 240g pollo + batata 450g + verduras\n" +
                            "Opción 4: Atún 240g + 400g papa + aguacate 100g\n" +
                            "Opción 5: 240g pollo + ensalada + aceite oliva 10g + pan integral 140g"
            );
        }else if (calorias <= 3400) {

            txtDesayuno.setText(
                    "Opción 1: 7 huevos + 3 arepas (200g)\n" +
                            "Opción 2: Avena 130g + banano + mantequilla de maní 40g\n" +
                            "Opción 3: Pan integral 200g + 5 huevos + aguacate 100g\n" +
                            "Opción 4: Yogur griego 500g + avena 110g + frutos rojos + nueces 40g\n" +
                            "Opción 5: Arepa 200g + 5 huevos + queso 100g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 280g pollo + 280g arroz + ensalada + aguacate 110g\n" +
                            "Opción 2: 280g carne magra + 260g arroz + plátano 250g\n" +
                            "Opción 3: 280g pollo + pasta integral 280g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 280g carne + 280g arroz + lentejas 200g\n" +
                            "Opción 5: 280g pollo + 500g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 280g pescado + verduras + papa 450g\n" +
                            "Opción 2: 260g carne magra + 500g papa + ensalada\n" +
                            "Opción 3: 260g pollo + batata 500g + verduras\n" +
                            "Opción 4: Atún 260g + 450g papa + aguacate 110g\n" +
                            "Opción 5: 260g pollo + ensalada + aceite oliva 10g + pan integral 160g"
            );
        }else if (calorias <= 3600) {

            txtDesayuno.setText(
                    "Opción 1: 8 huevos + 3 arepas (220g)\n" +
                            "Opción 2: Avena 140g + banano + mantequilla de maní 45g\n" +
                            "Opción 3: Pan integral 220g + 6 huevos + aguacate 110g\n" +
                            "Opción 4: Yogur griego 550g + avena 120g + frutos rojos + nueces 45g\n" +
                            "Opción 5: Arepa 220g + 6 huevos + queso 110g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 300g pollo + 300g arroz + ensalada + aguacate 120g\n" +
                            "Opción 2: 300g carne magra + 280g arroz + plátano 280g\n" +
                            "Opción 3: 300g pollo + pasta integral 300g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 300g carne + 300g arroz + lentejas 220g\n" +
                            "Opción 5: 300g pollo + 550g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 300g pescado + verduras + papa 500g\n" +
                            "Opción 2: 280g carne magra + 550g papa + ensalada\n" +
                            "Opción 3: 280g pollo + batata 550g + verduras\n" +
                            "Opción 4: Atún 280g + 500g papa + aguacate 120g\n" +
                            "Opción 5: 280g pollo + ensalada + aceite oliva 10g + pan integral 180g"
            );
        }else if (calorias <= 3800) {

            txtDesayuno.setText(
                    "Opción 1: 8 huevos + 4 arepas (240g)\n" +
                            "Opción 2: Avena 150g + banano + mantequilla de maní 50g\n" +
                            "Opción 3: Pan integral 240g + 6 huevos + aguacate 120g\n" +
                            "Opción 4: Yogur griego 600g + avena 130g + frutos rojos + nueces 50g\n" +
                            "Opción 5: Arepa 240g + 6 huevos + queso 120g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 320g pollo + 320g arroz + ensalada + aguacate 130g\n" +
                            "Opción 2: 320g carne magra + 300g arroz + plátano 300g\n" +
                            "Opción 3: 320g pollo + pasta integral 320g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 320g carne + 320g arroz + lentejas 240g\n" +
                            "Opción 5: 320g pollo + 600g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 320g pescado + verduras + papa 550g\n" +
                            "Opción 2: 300g carne magra + 600g papa + ensalada\n" +
                            "Opción 3: 300g pollo + batata 600g + verduras\n" +
                            "Opción 4: Atún 300g + 550g papa + aguacate 130g\n" +
                            "Opción 5: 300g pollo + ensalada + aceite oliva 10g + pan integral 200g"
            );

        } else {

            txtDesayuno.setText(
                    "Opción 1: 9 huevos + 4 arepas (260g)\n" +
                            "Opción 2: Avena 160g + banano + mantequilla de maní 60g\n" +
                            "Opción 3: Pan integral 260g + 7 huevos + aguacate 140g\n" +
                            "Opción 4: Yogur griego 650g + avena 140g + frutos rojos + nueces 60g\n" +
                            "Opción 5: Arepa 260g + 7 huevos + queso 140g"
            );

            txtAlmuerzo.setText(
                    "Opción 1: 350g pollo + 350g arroz + ensalada + aguacate 140g\n" +
                            "Opción 2: 350g carne magra + 320g arroz + plátano 320g\n" +
                            "Opción 3: 350g pollo + pasta integral 350g + verduras + aceite oliva 10g\n" +
                            "Opción 4: 350g carne + 350g arroz + lentejas 260g\n" +
                            "Opción 5: 350g pollo + 650g papa + ensalada + aceite oliva 10g"
            );

            txtCena.setText(
                    "Opción 1: 350g pescado + verduras + papa 600g\n" +
                            "Opción 2: 320g carne magra + 650g papa + ensalada\n" +
                            "Opción 3: 320g pollo + batata 650g + verduras\n" +
                            "Opción 4: Atún 320g + 600g papa + aguacate 140g\n" +
                            "Opción 5: 320g pollo + ensalada + aceite oliva 10g + pan integral 220g"
            );
        }
    }
}