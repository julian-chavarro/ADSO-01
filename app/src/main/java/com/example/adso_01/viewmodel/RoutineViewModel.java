package com.example.adso_01.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adso_01.R;
import com.example.adso_01.model.Ejercicio;
import com.example.adso_01.model.Serie;
import com.example.adso_01.repository.FirebaseFirestoreErrorMapper;
import com.example.adso_01.repository.RoutineDataSource;
import com.example.adso_01.repository.RoutineRepository;
import com.example.adso_01.ui.common.Event;
import com.example.adso_01.util.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel para la gestión de entrenamientos activos.
 * <p>
 * Centraliza la lógica de obtención de rutinas, persistencia de series
 * completadas y ciclo de vida de la sesión de entrenamiento.
 * Se comunica con {@link RoutineDataSource} para obtener los ejercicios
 * y con {@link RoutineRepository} para persistir en Firestore.
 * </p>
 */
@HiltViewModel
public class RoutineViewModel extends ViewModel {

    private final RoutineRepository repository;
    private final RoutineDataSource routineDataSource;
    private final Application application;

    private final MutableLiveData<Resource<Void>> saveSerieState = new MutableLiveData<>(Resource.idle());

    private final MutableLiveData<Event<Boolean>> saveSerieSuccess = new MutableLiveData<>();

    private final MutableLiveData<Resource<Void>> sessionState = new MutableLiveData<>(Resource.idle());

    private String fechaSesionActual;

    @Inject
    public RoutineViewModel(RoutineRepository repository, RoutineDataSource routineDataSource, Application application) {
        this.repository = repository;
        this.routineDataSource = routineDataSource;
        this.application = application;
        this.fechaSesionActual = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    // ─── Exposición de LiveData ───────────────────────────────────────────

    public LiveData<Resource<Void>> getSaveSerieState() { return saveSerieState; }

    public LiveData<Event<Boolean>> getSaveSerieSuccess() { return saveSerieSuccess; }

    public LiveData<Resource<Void>> getSessionState() { return sessionState; }

    public List<Ejercicio> obtenerRutinaPorGrupoMuscular(String grupoMuscular, String sexo, String objetivo) {
        return routineDataSource.obtenerRutinaPorGrupoMuscular(grupoMuscular, sexo, objetivo);
    }

    public void saveSerie(String nombreEjercicio, Serie serie, String weightInput) {
        if (weightInput == null || weightInput.trim().isEmpty()) {
            saveSerieState.setValue(Resource.error(application.getString(R.string.error_weight_empty)));
            return;
        }

        try {
            double peso = Double.parseDouble(weightInput.trim());
            serie.setPeso(peso);
            serie.setCompletada(true);

            saveSerieState.setValue(Resource.loading());
            repository.registrarSerie(nombreEjercicio, serie, (success, error) -> {
                if (success) {
                    saveSerieState.postValue(Resource.success(null));
                    saveSerieSuccess.postValue(new Event<>(true));
                } else {
                    serie.setCompletada(false);
                    saveSerieState.postValue(Resource.error(FirebaseFirestoreErrorMapper.toMessage(application, error)));
                }
            });
        } catch (NumberFormatException e) {
            saveSerieState.setValue(Resource.error(application.getString(R.string.error_weight_invalid)));
        }
    }

    public void iniciarSesion(int totalEjercicios, String diaNombre) {
        sessionState.setValue(Resource.loading());
        repository.iniciarSesion(fechaSesionActual, totalEjercicios, diaNombre, (success, error) -> {
            if (success) {
                sessionState.postValue(Resource.success(null));
            } else {
                sessionState.postValue(Resource.error(
                        FirebaseFirestoreErrorMapper.toMessage(application, error)));
            }
        });
    }

    public void finalizarSesion() {
        repository.finalizarSesion(fechaSesionActual, (success, error) -> {
            if (success) {
                sessionState.postValue(Resource.success(null));
            } else {
                sessionState.postValue(Resource.error(
                        FirebaseFirestoreErrorMapper.toMessage(application, error)));
            }
        });
    }

    public void guardarProgresoEjercicio(String nombreEjercicio, String grupoMuscular, List<Serie> seriesCompletadas) {
        if (seriesCompletadas == null || seriesCompletadas.isEmpty()) return;

        double pesoMaximo = 0;
        double volumenTotal = 0;
        int repeticionesTotales = 0;
        int seriesCount = 0;

        for (Serie s : seriesCompletadas) {
            if (s.isCompletada()) {
                if (s.getPeso() > pesoMaximo) pesoMaximo = s.getPeso();
                volumenTotal += s.getPeso() * s.getRepeticiones();
                repeticionesTotales += s.getRepeticiones();
                seriesCount++;
            }
        }

        if (seriesCount == 0) return;

        repository.guardarProgresoEjercicio(
                nombreEjercicio, grupoMuscular, fechaSesionActual,
                pesoMaximo, volumenTotal, repeticionesTotales, seriesCount,
                (success, error) -> {});
    }

    public void clearSaveSerieState() {
        saveSerieState.setValue(Resource.idle());
    }

    public void clearSessionState() {
        sessionState.setValue(Resource.idle());
    }
}
