package com.example.adso_01.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

/**
 * ViewModel para la gestión de entrenamientos.
 * Centraliza la lógica de rutinas y persistencia de series.
 */
public class RoutineViewModel extends AndroidViewModel {

    private final RoutineRepository repository;
    private final RoutineDataSource routineDataSource = new RoutineDataSource();

    private final MutableLiveData<Resource<Void>> saveSerieState = new MutableLiveData<>(Resource.idle());
    private final MutableLiveData<Event<Boolean>> saveSerieSuccess = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> sessionState = new MutableLiveData<>(Resource.idle());

    private String fechaSesionActual;

    public RoutineViewModel(@NonNull Application application) {
        super(application);
        this.repository = new RoutineRepository();
        this.fechaSesionActual = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public LiveData<Resource<Void>> getSaveSerieState() { return saveSerieState; }
    public LiveData<Event<Boolean>> getSaveSerieSuccess() { return saveSerieSuccess; }
    public LiveData<Resource<Void>> getSessionState() { return sessionState; }

    /**
     * Obtiene una rutina para el grupo muscular seleccionado.
     */
    public List<Ejercicio> obtenerRutinaPorGrupoMuscular(String grupoMuscular) {
        return routineDataSource.obtenerRutinaPorGrupoMuscular(grupoMuscular);
    }

    /**
     * Obtiene la rutina sugerida delegando la lógica al DataSource.
     */
    public List<Ejercicio> obtenerRutinaSugerida(String objetivo, String diaForzado) {
        return routineDataSource.obtenerRutinaSugerida(objetivo, diaForzado);
    }

    /**
     * Valida y guarda una serie en Firestore.
     */
    public void saveSerie(String nombreEjercicio, Serie serie, String weightInput) {
        if (weightInput == null || weightInput.trim().isEmpty()) {
            saveSerieState.setValue(Resource.error(getApplication().getString(R.string.error_weight_empty)));
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
                    saveSerieState.postValue(Resource.error(FirebaseFirestoreErrorMapper.toMessage(getApplication(), error)));
                }
            });
        } catch (NumberFormatException e) {
            saveSerieState.setValue(Resource.error(getApplication().getString(R.string.error_weight_invalid)));
        }
    }

    /**
     * Registra el inicio de una sesión de entrenamiento en Firestore.
     */
    public void iniciarSesion(int totalEjercicios, String diaNombre) {
        sessionState.setValue(Resource.loading());
        repository.iniciarSesion(fechaSesionActual, totalEjercicios, diaNombre, (success, error) -> {
            if (success) {
                sessionState.postValue(Resource.success(null));
            } else {
                sessionState.postValue(Resource.error(
                        FirebaseFirestoreErrorMapper.toMessage(getApplication(), error)));
            }
        });
    }

    /**
     * Marca la sesión actual como completada.
     */
    public void finalizarSesion() {
        repository.finalizarSesion(fechaSesionActual, (success, error) -> {
            if (success) {
                sessionState.postValue(Resource.success(null));
            } else {
                sessionState.postValue(Resource.error(
                        FirebaseFirestoreErrorMapper.toMessage(getApplication(), error)));
            }
        });
    }

    /**
     * Guarda el progreso de un ejercicio (todas sus series) en la colección plana.
     */
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
                (success, error) -> {
                    // Silencioso: no bloquear al usuario por esto
                });
    }

    public void clearSaveSerieState() {
        saveSerieState.setValue(Resource.idle());
    }

    public void clearSessionState() {
        sessionState.setValue(Resource.idle());
    }
}
