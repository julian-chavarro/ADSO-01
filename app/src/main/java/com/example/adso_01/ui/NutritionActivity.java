package com.example.adso_01.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.model.MealOption;
import com.example.adso_01.model.NutritionPlan;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.viewmodel.NutritionViewModel;
import com.example.adso_01.viewmodel.UserViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Actividad que muestra el plan nutricional completo del usuario con 5 opciones
 * por comida (desayuno, almuerzo, cena) y tracking de progreso diario.
 * <p>
 * Sigue MVVM: la Activity solo observa datos del {@link NutritionViewModel}
 * y delega toda la lógica de negocio y persistencia al ViewModel y sus
 * repositorios. No contiene datos nutricionales hardcodeados.
 * </p>
 */
@AndroidEntryPoint
public class NutritionActivity extends AppCompatActivity {

    // ─── Vistas ──────────────────────────────────────────────────────────

    private TextView txtObjetivo, txtCalorias, txtProgreso;
    private Button btnDesayuno, btnAlmuerzo, btnCena, btnVolver;
    private LinearLayout layoutDesayuno, layoutAlmuerzo, layoutCena;

    /** 5 TextViews por comida, poblados desde el ViewModel. */
    private TextView[] txtDesayunos = new TextView[5];
    private TextView[] txtAlmuerzos = new TextView[5];
    private TextView[] txtCenas = new TextView[5];

    // ─── ViewModels ──────────────────────────────────────────────────────

    private UserViewModel userViewModel;
    private NutritionViewModel nutritionViewModel;

    // ─── Estado local de UI ──────────────────────────────────────────────

    private boolean desayunoCompleto = false;
    private boolean almuerzoCompleto = false;
    private boolean cenaCompleta = false;

    // ═════════════════════════════════════════════════════════════════════
    //  Ciclo de vida
    // ═════════════════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        initViews();
        initViewModels();
        setupObservers();

        // Cargar datos del usuario desde Firestore
        userViewModel.loadUser();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Inicialización
    // ═════════════════════════════════════════════════════════════════════

    /** Vincula todas las vistas del layout. */
    private void initViews() {
        txtObjetivo = findViewById(R.id.txtObjetivo);
        txtCalorias = findViewById(R.id.txtCalorias);
        txtProgreso = findViewById(R.id.txtProgreso);

        btnDesayuno = findViewById(R.id.btnDesayuno);
        btnAlmuerzo = findViewById(R.id.btnAlmuerzo);
        btnCena = findViewById(R.id.btnCena);
        btnVolver = findViewById(R.id.btnVolver);

        layoutDesayuno = findViewById(R.id.layoutDesayuno);
        layoutAlmuerzo = findViewById(R.id.layoutAlmuerzo);
        layoutCena = findViewById(R.id.layoutCena);

        // TextViews de opciones de comida — se llenan desde el ViewModel
        txtDesayunos[0] = findViewById(R.id.txtDesayuno1);
        txtDesayunos[1] = findViewById(R.id.txtDesayuno2);
        txtDesayunos[2] = findViewById(R.id.txtDesayuno3);
        txtDesayunos[3] = findViewById(R.id.txtDesayuno4);
        txtDesayunos[4] = findViewById(R.id.txtDesayuno5);

        txtAlmuerzos[0] = findViewById(R.id.txtAlmuerzo1);
        txtAlmuerzos[1] = findViewById(R.id.txtAlmuerzo2);
        txtAlmuerzos[2] = findViewById(R.id.txtAlmuerzo3);
        txtAlmuerzos[3] = findViewById(R.id.txtAlmuerzo4);
        txtAlmuerzos[4] = findViewById(R.id.txtAlmuerzo5);

        txtCenas[0] = findViewById(R.id.txtCena1);
        txtCenas[1] = findViewById(R.id.txtCena2);
        txtCenas[2] = findViewById(R.id.txtCena3);
        txtCenas[3] = findViewById(R.id.txtCena4);
        txtCenas[4] = findViewById(R.id.txtCena5);

        // Botón volver
        btnVolver.setOnClickListener(v -> finish());
    }

    /** Inicializa ambos ViewModels. */
    private void initViewModels() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        nutritionViewModel = new ViewModelProvider(this).get(NutritionViewModel.class);
    }

    /** Configura los observadores de ambos ViewModels. */
    private void setupObservers() {
        // Observar datos del usuario para obtener calorías, sexo y objetivo
        userViewModel.getUserState().observe(this, resource -> {
            if (resource == null) return;

            if (resource.isSuccess() && resource.getData() != null) {
                Usuario usuario = resource.getData();
                if (usuario.getCalorias() > 0) {
                    txtObjetivo.setText(getString(R.string.nutrition_objetivo_format, usuario.getObjetivo()));
                    txtCalorias.setText(getString(R.string.nutrition_calorias_format, usuario.getCalorias()));

                    // Cargar plan nutricional desde el DataSource
                    nutritionViewModel.cargarPlan(
                            usuario.getCalorias(),
                            usuario.getSexo(),
                            usuario.getObjetivo()
                    );
                } else {
                    Toast.makeText(this, R.string.error_no_goals, Toast.LENGTH_LONG).show();
                }
            } else if (resource.isError()) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Observar el plan nutricional
        nutritionViewModel.getPlanState().observe(this, resource -> {
            if (resource == null) return;

            if (resource.isSuccess() && resource.getData() != null) {
                NutritionPlan plan = resource.getData();
                mostrarOpciones(plan.getDesayunos(), txtDesayunos, layoutDesayuno);
                mostrarOpciones(plan.getAlmuerzos(), txtAlmuerzos, layoutAlmuerzo);
                mostrarOpciones(plan.getCenas(), txtCenas, layoutCena);
            }
        });

        // Observar el progreso nutricional del día
        nutritionViewModel.getProgresoState().observe(this, progreso -> {
            if (progreso == null) return;
            if (progreso >= 100) {
                txtProgreso.setText("✅ ¡Meta nutricional cumplida!");
            } else {
                txtProgreso.setText("Progreso del día: " + progreso + "%");
            }
        });

        // Observar el estado de comidas para actualizar botones
        nutritionViewModel.getEstadoComidasState().observe(this, estado -> {
            if (estado == null) return;
            desayunoCompleto = estado[0];
            almuerzoCompleto = estado[1];
            cenaCompleta = estado[2];

            btnDesayuno.setText(estado[0] ? "✅" : "Completar");
            btnDesayuno.setEnabled(!estado[0]);

            btnAlmuerzo.setText(estado[1] ? "✅" : "Completar");
            btnAlmuerzo.setEnabled(!estado[1]);

            btnCena.setText(estado[2] ? "✅" : "Completar");
            btnCena.setEnabled(!estado[2]);
        });

        // Click listeners para marcar comidas
        btnDesayuno.setOnClickListener(v -> {
            nutritionViewModel.marcarComida("desayuno");
            desayunoCompleto = true;
        });
        btnAlmuerzo.setOnClickListener(v -> {
            nutritionViewModel.marcarComida("almuerzo");
            almuerzoCompleto = true;
        });
        btnCena.setOnClickListener(v -> {
            nutritionViewModel.marcarComida("cena");
            cenaCompleta = true;
        });
    }

    // ═════════════════════════════════════════════════════════════════════
    //  UI — Plan nutricional
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Muestra las opciones de comida en los TextViews correspondientes,
     * cada uno envuelto en un MaterialCardView. El nombre de la opción
     * aparece en negrita y la descripción en texto normal.
     *
     * @param opciones  Lista de opciones de comida (máximo 5).
     * @param textViews Arreglo de 5 TextViews para mostrar las opciones.
     * @param container Contenedor LinearLayout a ocultar si no hay datos.
     */
    private void mostrarOpciones(List<MealOption> opciones, TextView[] textViews, LinearLayout container) {
        if (opciones == null || opciones.isEmpty()) {
            container.setVisibility(View.GONE);
            return;
        }

        container.setVisibility(View.VISIBLE);

        int count = Math.min(opciones.size(), textViews.length);
        for (int i = 0; i < count; i++) {
            MealOption opcion = opciones.get(i);

            // Nombre en negrita + descripción normal con SpannableString
            String nombre = opcion.getNombre();
            String descripcion = opcion.getDescripcion();
            String textoCompleto = nombre + "\n" + descripcion;
            SpannableStringBuilder ssb = new SpannableStringBuilder(textoCompleto);
            ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, nombre.length(),
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);

            textViews[i].setText(ssb);
            textViews[i].setVisibility(View.VISIBLE);

            // Mostrar el MaterialCardView que envuelve al TextView
            View parentCard = (View) textViews[i].getParent();
            if (parentCard != null) {
                parentCard.setVisibility(View.VISIBLE);
            }
        }

        // Ocultar cards sobrantes (si hay menos de 5 opciones)
        for (int i = count; i < textViews.length; i++) {
            textViews[i].setVisibility(View.GONE);
            View parentCard = (View) textViews[i].getParent();
            if (parentCard != null) {
                parentCard.setVisibility(View.GONE);
            }
        }
    }
}
