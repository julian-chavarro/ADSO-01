package com.example.adso_01.viewmodel;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adso_01.util.FitnessGoals;
import com.example.adso_01.model.Usuario;
import com.example.adso_01.repository.FirebaseFirestoreErrorMapper;
import com.example.adso_01.repository.UserRepository;
import com.example.adso_01.ui.common.Event;
import com.example.adso_01.util.Resource;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel encargado de la lógica de negocio del perfil de usuario y sus objetivos.
 * <p>
 * Gestiona la carga y guardado del perfil del usuario desde/hacia Firestore,
 * el cálculo de calorías recomendadas mediante la fórmula de Mifflin-St Jeor,
 * y la migración silenciosa de objetivos legacy.
 * Sigue el patrón MVVM exponiendo estados observables ({@link Resource}) para la UI.
 * </p>
 */
@HiltViewModel
public class UserViewModel extends ViewModel {

    private final UserRepository repository;
    private final Application application;

    private final MutableLiveData<Resource<Usuario>> userState = new MutableLiveData<>(Resource.idle());

    private final MutableLiveData<Resource<Void>> saveState = new MutableLiveData<>(Resource.idle());

    private final MutableLiveData<Event<Boolean>> saveSuccess = new MutableLiveData<>();

    @Inject
    public UserViewModel(UserRepository repository, Application application) {
        this.repository = repository;
        this.application = application;
    }

    // ─── Exposición de LiveData ───────────────────────────────────────────

    public LiveData<Resource<Usuario>> getUserState() { return userState; }

    public LiveData<Resource<Void>> getSaveState() { return saveState; }

    public LiveData<Event<Boolean>> getSaveSuccess() { return saveSuccess; }

    public void loadUser() {
        userState.setValue(Resource.loading());
        repository.obtenerUsuario((usuario, error) -> {
            if (error != null) {
                userState.postValue(Resource.error(FirebaseFirestoreErrorMapper.toMessage(application, error)));
                return;
            }
            if (usuario != null && usuario.getObjetivo() != null) {
                migrarObjetivoLegacy(usuario);
            }
            userState.postValue(Resource.success(usuario));
        });
    }

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
                saveState.postValue(Resource.error(FirebaseFirestoreErrorMapper.toMessage(application, error)));
            }
        });
    }

    public int calcularCaloriasRecomendadas(int edad, int peso, int estatura, String sexo, String actividad, String objetivo) {
        double bmr = "Masculino".equals(sexo)
                ? (10 * peso) + (6.25 * estatura) - (5 * edad) + 5
                : (10 * peso) + (6.25 * estatura) - (5 * edad) - 161;

        double factorActividad = 1.2;
        switch (actividad) {
            case "Ligera":   factorActividad = 1.375; break;
            case "Moderada": factorActividad = 1.55;  break;
            case "Intensa":  factorActividad = 1.725; break;
        }

        double total = bmr * factorActividad;
        total += FitnessGoals.getAjusteCalorico(objetivo);

        return (int) total;
    }

    public Usuario buildUsuarioFromForm(int edad, int peso, int estatura, String sexo, String actividad, String objetivo) {
        int calorias = calcularCaloriasRecomendadas(edad, peso, estatura, sexo, actividad, objetivo);
        return new Usuario(edad, peso, estatura, sexo, actividad, objetivo, calorias);
    }

    public void clearUserState() { userState.setValue(Resource.idle()); }

    public void clearSaveState() { saveState.setValue(Resource.idle()); }

    private void migrarObjetivoLegacy(Usuario usuario) {
        String obj = usuario.getObjetivo();
        if (FitnessGoals.GANAR_MUSCULO.equals(obj)) {
            usuario.setObjetivo(FitnessGoals.GANAR_MASA_MUSCULAR);
        }
    }
}
