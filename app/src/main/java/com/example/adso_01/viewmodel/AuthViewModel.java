package com.example.adso_01.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.adso_01.repository.AuthOperationCallback;
import com.example.adso_01.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public void resetPassword(String email, AuthOperationCallback callback) {
        repository.resetPassword(email, callback);
    }

    public void login(String email, String password, AuthOperationCallback callback) {
        repository.login(email, password, callback);
    }

    public void register(String email, String password, AuthOperationCallback callback) {
        repository.register(email, password, callback);
    }
}
