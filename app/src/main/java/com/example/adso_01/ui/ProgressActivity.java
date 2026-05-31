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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressActivity extends AppCompatActivity {

    private ProgressViewModel viewModel;

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

        Button btnBack = findViewById(R.id.btnBackProgress);
        btnBack.setOnClickListener(v -> finish());

        rvSesiones.setLayoutManager(new LinearLayoutManager(this));
        rvProgresoEjercicios.setLayoutManager(new LinearLayoutManager(this));

        sesionAdapter = new SesionAdapter(new ArrayList<>());
        rvSesiones.setAdapter(sesionAdapter);

        progresoAdapter = new ProgresoEjercicioAdapter(new ArrayList<>());
        rvProgresoEjercicios.setAdapter(progresoAdapter);

        cargarDatos();
    }

    private void initViews() {
        rvSesiones = findViewById(R.id.rvSesiones);
        rvProgresoEjercicios = findViewById(R.id.rvProgresoEjercicios);
        tvEmptySesiones = findViewById(R.id.tvEmptySesiones);
        tvEmptyProgreso = findViewById(R.id.tvEmptyProgreso);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupViewModels() {
        viewModel = new ViewModelProvider(this).get(ProgressViewModel.class);

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

        viewModel.getProgresoState().observe(this, resource -> {
            if (resource == null) return;

            if (resource.isLoading()) {
                progressBar.setVisibility(View.VISIBLE);
                return;
            }

            progressBar.setVisibility(View.GONE);

            if (resource.isSuccess()) {
                List<ProgresoEjercicio> rawList = resource.getData();
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

    private void cargarDatos() {
        viewModel.cargarSesiones();
        viewModel.cargarProgresoEjercicios();
    }

    private List<ProgresoEjercicio> agregarProgreso(List<ProgresoEjercicio> rawList) {
        if (rawList == null || rawList.isEmpty()) return new ArrayList<>();

        Map<String, ProgresoEjercicio> grouped = new HashMap<>();

        for (ProgresoEjercicio p : rawList) {
            String key = p.getNombreEjercicio();
            ProgresoEjercicio existing = grouped.get(key);

            if (existing == null) {
                grouped.put(key, p);
            } else {
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
