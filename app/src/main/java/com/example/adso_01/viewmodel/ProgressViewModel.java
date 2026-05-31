package com.example.adso_01.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.adso_01.R;
import com.example.adso_01.model.ProgresoEjercicio;
import com.example.adso_01.model.ResumenSesion;
import com.example.adso_01.repository.FirebaseFirestoreErrorMapper;
import com.example.adso_01.repository.ProgressRepository;
import com.example.adso_01.util.Resource;

import java.util.List;

public class ProgressViewModel extends AndroidViewModel {

    private final ProgressRepository repository;

    private final MutableLiveData<Resource<List<ResumenSesion>>> sesionesState = new MutableLiveData<>(Resource.idle());
    private final MutableLiveData<Resource<List<ProgresoEjercicio>>> progresoState = new MutableLiveData<>(Resource.idle());

    public ProgressViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ProgressRepository();
    }

    public LiveData<Resource<List<ResumenSesion>>> getSesionesState() { return sesionesState; }
    public LiveData<Resource<List<ProgresoEjercicio>>> getProgresoState() { return progresoState; }

    public void cargarSesiones() {
        sesionesState.setValue(Resource.loading());
        repository.obtenerSesiones((sesiones, error) -> {
            if (error != null) {
                sesionesState.postValue(Resource.error(
                        FirebaseFirestoreErrorMapper.toMessage(getApplication(), error)));
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
                        FirebaseFirestoreErrorMapper.toMessage(getApplication(), error)));
            } else {
                progresoState.postValue(Resource.success(progresoList));
            }
        });
    }
}
