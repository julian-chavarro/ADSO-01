package com.example.adso_01.repository;

import com.example.adso_01.model.ResumenSesion;

import java.util.List;

public interface ProgressSesionesCallback {
    void onResult(List<ResumenSesion> sesiones, Exception error);
}
