package com.example.adso_01.repository;

import com.example.adso_01.model.ProgresoEjercicio;

import java.util.List;

public interface ProgressEjerciciosCallback {
    void onResult(List<ProgresoEjercicio> progreso, Exception error);
}
