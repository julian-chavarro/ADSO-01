package com.example.adso_01.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.adso_01.R;
import com.example.adso_01.model.FitnessGoals;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.repository.FirebaseFirestoreErrorMapper;
import com.example.adso_01.repository.UserRepository;
import com.example.adso_01.ui.common.Event;
import com.example.adso_01.util.Resource;

/**
 * ViewModel encargado de gestionar la lógica de negocio del perfil de usuario y sus objetivos.
 * Sigue el patrón MVVM y las directrices de modularización de AGENTS.md.
 *
 * IMPORTANTE: NO normaliza el objetivo en la lectura.
 * El valor exacto que el usuario seleccionó se preserva y se muestra tal cual.
 */
public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;
    private final MutableLiveData<Resource<Usuario>> userState = new MutableLiveData<>(Resource.idle());
    private final MutableLiveData<Resource<Void>> saveState = new MutableLiveData<>(Resource.idle());
    private final MutableLiveData<Event<Boolean>> saveSuccess = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository();
    }

    public LiveData<Resource<Usuario>> getUserState() { return userState; }
    public LiveData<Resource<Void>> getSaveState() { return saveState; }
    public LiveData<Event<Boolean>> getSaveSuccess() { return saveSuccess; }

    /**
     * Carga el perfil del usuario desde Firestore.
     * NO normaliza el objetivo — se muestra exactamente lo que el usuario seleccionó.
     */
    public void loadUser() {
        userState.setValue(Resource.loading());
        repository.obtenerUsuario((usuario, error) -> {
            if (error != null) {
                userState.postValue(Resource.error(FirebaseFirestoreErrorMapper.toMessage(getApplication(), error)));
                return;
            }
            // Solo migrar datos legacy (por ejemplo, "Ganar músculo" → "Ganar masa muscular")
            // sin sobrescribir objetivos ya correctos.
            if (usuario != null && usuario.getObjetivo() != null) {
                migrarObjetivoLegacy(usuario);
            }
            userState.postValue(Resource.success(usuario));
        });
    }

    /**
     * Guarda el usuario en Firestore preservando el objetivo exacto.
     */
    public void saveUser(@Nullable Usuario usuario) {
        if (usuario == null) {
            saveState.setValue(Resource.error("Datos de usuario no válidos"));
            return;
        }

        saveState.setValue(Resource.loading());

        repository.guardarUsuario(usuario, (success, error) -> {
            if (success) {
                saveState.postValue(Resource.success(null));
                saveSuccess.postValue(new Event<>(true));
                userState.postValue(Resource.success(usuario));
            } else {
                saveState.postValue(Resource.error(FirebaseFirestoreErrorMapper.toMessage(getApplication(), error)));
            }
        });
    }

    /**
     * Calcula las calorías recomendadas basadas en parámetros físicos y objetivo.
     * Implementa la fórmula de Mifflin-St Jeor.
     * Usa FitnessGoals.getAjusteCalorico() para soportar cualquier string de objetivo.
     */
    public int calcularCaloriasRecomendadas(int edad, int peso, int estatura, String sexo, String actividad, String objetivo) {
        double bmr = "Masculino".equals(sexo)
                ? (10 * peso) + (6.25 * estatura) - (5 * edad) + 5
                : (10 * peso) + (6.25 * estatura) - (5 * edad) - 161;

        double factorActividad = 1.2;
        switch (actividad) {
            case "Ligera": factorActividad = 1.375; break;
            case "Moderada": factorActividad = 1.55; break;
            case "Intensa": factorActividad = 1.725; break;
        }

        double total = bmr * factorActividad;
        total += FitnessGoals.getAjusteCalorico(objetivo);

        return (int) total;
    }

    /**
     * Construye un objeto Usuario a partir de los datos del formulario.
     */
    public Usuario buildUsuarioFromForm(int edad, int peso, int estatura, String sexo, String actividad, String objetivo) {
        int calorias = calcularCaloriasRecomendadas(edad, peso, estatura, sexo, actividad, objetivo);
        return new Usuario(edad, peso, estatura, sexo, actividad, objetivo, calorias);
    }

    public void clearUserState() { userState.setValue(Resource.idle()); }
    public void clearSaveState() { saveState.setValue(Resource.idle()); }

    /**
     * Migración silenciosa de objetivos legacy:
     * "Ganar músculo" → "Ganar masa muscular"
     * Solo se ejecuta si el valor coincide exactamente con el legacy.
     * NO toca objetivos que ya sean correctos o personalizados.
     */
    private void migrarObjetivoLegacy(Usuario usuario) {
        String obj = usuario.getObjetivo();
        if (FitnessGoals.GANAR_MUSCULO.equals(obj)) {
            usuario.setObjetivo(FitnessGoals.GANAR_MASA_MUSCULAR);
        }
        // "Perder grasa" y "Mantener peso" ya son idénticos — no requieren migración.
        // "Recomposición corporal" no existía antes — no requiere migración.
    }
}
