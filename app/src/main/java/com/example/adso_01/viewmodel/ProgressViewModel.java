package com.example.adso_01.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adso_01.model.ProgresoEjercicio;
import com.example.adso_01.model.ResumenSesion;
import com.example.adso_01.repository.FirebaseFirestoreErrorMapper;
import com.example.adso_01.repository.ProgressRepository;
import com.example.adso_01.util.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel para la pantalla de Progreso.
 * <p>
 * Gestiona la carga del historial de sesiones de entrenamiento y del
 * progreso detallado por ejercicio desde Firestore. Expone estados
 * observables ({@link Resource}) para que la UI pueda mostrar
 * indicadores de carga, datos o errores según corresponda.
 * </p>
 */
@HiltViewModel
public class ProgressViewModel extends ViewModel {

    private final ProgressRepository repository;
    private final Application application;

    private final MutableLiveData<Resource<List<ResumenSesion>>> sesionesState =
            new MutableLiveData<>(Resource.idle());

    private final MutableLiveData<Resource<List<ProgresoEjercicio>>> progresoState =
            new MutableLiveData<>(Resource.idle());

    @Inject
    public ProgressViewModel(ProgressRepository repository, Application application) {
        this.repository = repository;
        this.application = application;
    }

    // ─── Exposición de LiveData ───────────────────────────────────────────

    public LiveData<Resource<List<ResumenSesion>>> getSesionesState() { return sesionesState; }

    public LiveData<Resource<List<ProgresoEjercicio>>> getProgresoState() { return progresoState; }

    public void cargarSesiones() {
        sesionesState.setValue(Resource.loading());
        repository.obtenerSesiones((sesiones, error) -> {
            if (error != null) {
                sesionesState.postValue(Resource.error(
                        FirebaseFirestoreErrorMapper.toMessage(application, error)));
            } else {
                sesionesState.postValue(Resource.success(sesiones));
            }
        });
    }

    public void cargarProgresoEjercicios() {
        progresoState.setValue(Resource.loading());
        repository.obtenerProgresoEjercicios((progresoList, error) -> {
            if (error != null) {
                progresoState.postValue(Resource.error(
                        FirebaseFirestoreErrorMapper.toMessage(application, error)));
            } else {
                progresoState.postValue(Resource.success(progresoList));
            }
        });
    }
}
