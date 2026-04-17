package com.example.adso_01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NutritionActivity extends AppCompatActivity {

    TextView txtObjetivo, txtCalorias, txtDesayuno, txtAlmuerzo, txtCena, txtProgreso;
    Button btnDesayuno, btnAlmuerzo, btnCena;
    ImageButton btnVolver;

    FirebaseFirestore db;
    FirebaseAuth auth;

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

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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

        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        String objetivo = doc.getString("objetivo");
                        Long calorias = doc.getLong("calorias");

                        txtObjetivo.setText("Objetivo: " + objetivo);
                        txtCalorias.setText("Calorías: " + calorias + " kcal");

                        if (calorias != null) {
                            generarDieta(calorias.intValue());
                        }
                    }
                });
    }

    private void generarDieta(int calorias) {

        if (calorias >= 1800 && calorias <= 2000) {

            txtDesayuno.setText("Opción 1: 3 huevos + 1 arepa\nOpción 2: Avena 50g + banano");
            txtAlmuerzo.setText("100g pollo + 100g arroz + ensalada");
            txtCena.setText("150g pescado + verduras");

        } else if (calorias <= 2200) {

            txtDesayuno.setText("4 huevos + 2 arepas\nAvena 70g + fruta");
            txtAlmuerzo.setText("150g carne + 150g arroz + lentejas");
            txtCena.setText("200g pollo + ensalada");

        } else if (calorias <= 2600) {

            txtDesayuno.setText("5 huevos + 2 arepas + queso");
            txtAlmuerzo.setText("200g pollo + 200g arroz + aguacate");
            txtCena.setText("200g carne + verduras");

        } else if (calorias <= 3000) {

            txtDesayuno.setText("6 huevos + 3 arepas + jugo");
            txtAlmuerzo.setText("250g pollo + 250g arroz + lentejas");
            txtCena.setText("250g pescado + ensalada");

        } else {

            txtDesayuno.setText("7 huevos + avena + fruta");
            txtAlmuerzo.setText("350g carne + 300g arroz + lentejas");
            txtCena.setText("300g pollo + ensalada");
        }
    }
}