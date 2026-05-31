package com.example.adso_01.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.adso_01.R;
import com.example.adso_01.repository.AuthRepository;
import com.example.adso_01.repository.FirebaseAuthErrorMapper;
import com.example.adso_01.ui.common.Event;
import com.example.adso_01.util.Resource;

/**
 * ViewModel para la gestión de autenticación.
 * Sigue el patrón MVVM y las directrices de AGENTS.md.
 */
public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;

    private final MutableLiveData<Resource<Void>> loginState =
            new MutableLiveData<>(Resource.idle());
    private final MutableLiveData<Event<Boolean>> loginSuccess = new MutableLiveData<>();

    private final MutableLiveData<Resource<Void>> registerState =
            new MutableLiveData<>(Resource.idle());
    private final MutableLiveData<Event<Boolean>> registerSuccess = new MutableLiveData<>();

    private final MutableLiveData<Resource<Void>> resetPasswordState =
            new MutableLiveData<>(Resource.idle());
    private final MutableLiveData<Event<Boolean>> resetPasswordSuccess = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository();
    }

    public LiveData<Resource<Void>> getLoginState() { return loginState; }
    public LiveData<Event<Boolean>> getLoginSuccess() { return loginSuccess; }
    public LiveData<Resource<Void>> getRegisterState() { return registerState; }
    public LiveData<Event<Boolean>> getRegisterSuccess() { return registerSuccess; }
    public LiveData<Resource<Void>> getResetPasswordState() { return resetPasswordState; }
    public LiveData<Event<Boolean>> getResetPasswordSuccess() { return resetPasswordSuccess; }

    public void login(String email, String password) {
        if (isAnyBlank(email, password)) {
            loginState.setValue(Resource.error(getString(R.string.error_fields_empty)));
            return;
        }
        loginState.setValue(Resource.loading());
        repository.login(email.trim(), password, (success, error) -> {
            if (success) {
                loginState.postValue(Resource.success(null));
                loginSuccess.postValue(new Event<>(true));
            } else {
                loginState.postValue(Resource.error(FirebaseAuthErrorMapper.toMessage(getApplication(), error)));
            }
        });
    }

    public void register(String nombre, String email, String password) {
        if (isAnyBlank(nombre, email, password)) {
            registerState.setValue(Resource.error(getString(R.string.error_fields_empty)));
            return;
        }
        if (password.length() < 6) {
            registerState.setValue(Resource.error(getString(R.string.error_weak_password)));
            return;
        }
        registerState.setValue(Resource.loading());
        repository.register(nombre.trim(), email.trim(), password, (success, error) -> {
            if (success) {
                registerState.postValue(Resource.success(null));
                registerSuccess.postValue(new Event<>(true));
            } else {
                registerState.postValue(Resource.error(FirebaseAuthErrorMapper.toMessage(getApplication(), error)));
            }
        });
    }

    public void resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            resetPasswordState.setValue(Resource.error(getString(R.string.error_email_empty)));
            return;
        }
        resetPasswordState.setValue(Resource.loading());
        repository.resetPassword(email.trim(), (success, error) -> {
            if (success) {
                resetPasswordState.postValue(Resource.success(null));
                resetPasswordSuccess.postValue(new Event<>(true));
            } else {
                resetPasswordState.postValue(Resource.error(FirebaseAuthErrorMapper.toMessage(getApplication(), error)));
            }
        });
    }

    public void clearLoginState() { loginState.setValue(Resource.idle()); }
    public void clearRegisterState() { registerState.setValue(Resource.idle()); }
    public void clearResetPasswordState() { resetPasswordState.setValue(Resource.idle()); }

    private boolean isAnyBlank(String... values) {
        for (String v : values) {
            if (v == null || v.trim().isEmpty()) return true;
        }
        return false;
    }

    private String getString(@StringRes int resId) {
        return getApplication().getString(resId);
    }
}
