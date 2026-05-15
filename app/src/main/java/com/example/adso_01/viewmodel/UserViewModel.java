package com.example.adso_01.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.adso_01.model.Usuario;
import com.example.adso_01.repository.AuthOperationCallback;
import com.example.adso_01.repository.UserProfileCallback;
import com.example.adso_01.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository repository;

    public UserViewModel() {
        repository = new UserRepository();
    }

    /**
     * Guarda el usuario sin exponer clases de Firebase (como Task o OnCompleteListener).
     */
    public void guardarUsuario(Usuario usuario, AuthOperationCallback callback) {
        repository.guardarUsuario(usuario, callback);
    }

    public void obtenerUsuario(UserProfileCallback callback) {
        repository.obtenerUsuario(callback);
    }
}
