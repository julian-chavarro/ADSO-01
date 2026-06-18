package com.example.adso_01.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.ProgresoEjercicio;
import com.example.adso_01.model.ResumenSesion;
import com.example.adso_01.util.Resource;
import com.example.adso_01.viewmodel.ProgressViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Actividad que muestra el historial de progreso del usuario.
 * <p>
 * Presenta dos secciones en RecyclerViews separados:
 * <ol>
 *   <li><b>Sesiones:</b> lista de resúmenes de entrenamientos completados,
 *       mostrada mediante {@link SesionAdapter}.</li>
 *   <li><b>Progreso por ejercicio:</b> datos agregados de cada ejercicio
 *       (peso máximo, volumen total, repeticiones totales, series),
 *       mostrados mediante {@link ProgresoEjercicioAdapter}.</li>
 * </ol>
 * Los datos se agrupan por nombre de ejercicio usando {@link #agregarProgreso(List)}
 * para evitar duplicados y consolidar métricas.
 * </p>
 */
@AndroidEntryPoint
public class ProgressActivity extends AppCompatActivity {

    /** ViewModel que gestiona la carga de datos desde Firestore. */
    private ProgressViewModel viewModel;

    // ─── Vistas ──────────────────────────────────────────────────────────

    private RecyclerView rvSesiones;
    private RecyclerView rvProgresoEjercicios;
    private TextView tvEmptySesiones;
    private TextView tvEmptyProgreso;
    private ProgressBar progressBar;

    private SesionAdapter sesionAdapter;
    private ProgresoEjercicioAdapter progresoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        initViews();
        setupViewModels();

        // Botón para volver al Lobby
        Button btnBack = findViewById(R.id.btnBackProgress);
        btnBack.setOnClickListener(v -> finish());

        // Configurar LayoutManagers para ambos RecyclerViews
        rvSesiones.setLayoutManager(new LinearLayoutManager(this));
        rvProgresoEjercicios.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar adaptadores con listas vacías
        sesionAdapter = new SesionAdapter(new ArrayList<>());
        rvSesiones.setAdapter(sesionAdapter);

        progresoAdapter = new ProgresoEjercicioAdapter(new ArrayList<>());
        rvProgresoEjercicios.setAdapter(progresoAdapter);

        cargarDatos();
    }

    /** Vincula todas las vistas del layout. */
    private void initViews() {
        rvSesiones = findViewById(R.id.rvSesiones);
        rvProgresoEjercicios = findViewById(R.id.rvProgresoEjercicios);
        tvEmptySesiones = findViewById(R.id.tvEmptySesiones);
        tvEmptyProgreso = findViewById(R.id.tvEmptyProgreso);
        progressBar = findViewById(R.id.progressBar);
    }

    /** Configura el ViewModel y observa los estados de carga. */
    private void setupViewModels() {
        viewModel = new ViewModelProvider(this).get(ProgressViewModel.class);

        // Observar el estado de las sesiones cargadas
        viewModel.getSesionesState().observe(this, resource -> {
            if (resource == null) return;

            if (resource.isLoading()) {
                progressBar.setVisibility(View.VISIBLE);
                return;
            }

            progressBar.setVisibility(View.GONE);

            if (resource.isSuccess()) {
                List<ResumenSesion> sesiones = resource.getData();
                if (sesiones != null && !sesiones.isEmpty()) {
                    tvEmptySesiones.setVisibility(View.GONE);
                    rvSesiones.setVisibility(View.VISIBLE);
                    sesionAdapter = new SesionAdapter(sesiones);
                    rvSesiones.setAdapter(sesionAdapter);
                } else {
                    tvEmptySesiones.setVisibility(View.VISIBLE);
                    rvSesiones.setVisibility(View.GONE);
                }
            } else if (resource.isError()) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Observar el estado del progreso de ejercicios cargado
        viewModel.getProgresoState().observe(this, resource -> {
            if (resource == null) return;

            if (resource.isLoading()) {
                progressBar.setVisibility(View.VISIBLE);
                return;
            }

            progressBar.setVisibility(View.GONE);

            if (resource.isSuccess()) {
                List<ProgresoEjercicio> rawList = resource.getData();
                // Agregar datos: agrupar por nombre de ejercicio
                List<ProgresoEjercicio> aggregated = agregarProgreso(rawList);

                if (aggregated != null && !aggregated.isEmpty()) {
                    tvEmptyProgreso.setVisibility(View.GONE);
                    rvProgresoEjercicios.setVisibility(View.VISIBLE);
                    progresoAdapter = new ProgresoEjercicioAdapter(aggregated);
                    rvProgresoEjercicios.setAdapter(progresoAdapter);
                } else {
                    tvEmptyProgreso.setVisibility(View.VISIBLE);
                    rvProgresoEjercicios.setVisibility(View.GONE);
                }
            } else if (resource.isError()) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Dispara la carga de sesiones y progreso desde Firestore. */
    private void cargarDatos() {
        viewModel.cargarSesiones();
        viewModel.cargarProgresoEjercicios();
    }

    /**
     * Agrupa y agrega registros de progreso por nombre de ejercicio.
     * <p>
     * Para cada ejercicio, consolida:
     * <ul>
     *   <li><b>Peso máximo:</b> el valor más alto registrado.</li>
     *   <li><b>Volumen total:</b> suma de todos los volúmenes.</li>
     *   <li><b>Repeticiones totales:</b> suma de todas las repeticiones.</li>
     *   <li><b>Series completadas:</b> suma de todas las series.</li>
     * </ul>
     * Esto evita duplicados cuando un mismo ejercicio aparece en múltiples sesiones.
     * </p>
     *
     * @param rawList Lista plana de registros de progreso desde Firestore.
     * @return Lista agregada con un único registro por ejercicio.
     */
    private List<ProgresoEjercicio> agregarProgreso(List<ProgresoEjercicio> rawList) {
        if (rawList == null || rawList.isEmpty()) return new ArrayList<>();

        Map<String, ProgresoEjercicio> grouped = new HashMap<>();

        for (ProgresoEjercicio p : rawList) {
            String key = p.getNombreEjercicio();
            ProgresoEjercicio existing = grouped.get(key);

            if (existing == null) {
                // Primera ocurrencia: usar el registro tal cual
                grouped.put(key, p);
            } else {
                // Ocurrencias siguientes: agregar métricas
                if (p.getPesoMaximo() > existing.getPesoMaximo()) {
                    existing.setPesoMaximo(p.getPesoMaximo());
                }
                existing.setVolumenTotal(existing.getVolumenTotal() + p.getVolumenTotal());
                existing.setRepeticionesTotales(existing.getRepeticionesTotales() + p.getRepeticionesTotales());
                existing.setSeriesCompletadas(existing.getSeriesCompletadas() + p.getSeriesCompletadas());
            }
        }

        return new ArrayList<>(grouped.values());
    }
}
