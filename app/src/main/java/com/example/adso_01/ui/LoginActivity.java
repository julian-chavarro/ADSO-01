package com.example.adso_01.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.util.Navigator;
import com.example.adso_01.viewmodel.AuthViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity de inicio de sesión.
 * <p>
 * Permite al usuario autenticarse con correo electrónico y contraseña
 * mediante Firebase Authentication. Incluye funcionalidad de "Recordar
 * credenciales" que persiste en SharedPreferences para no requerir
 * inicio de sesión en cada uso.
 * Desde aquí se puede navegar a {@link RegisterActivity} (registro)
 * y a {@link ForgotPasswordActivity} (restablecer contraseña).
 * </p>
 */
@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    /** Nombre del archivo de preferencias para credenciales guardadas. */
    private static final String PREFS_NAME = "LoginPrefs";

    /** Clave para guardar el correo electrónico recordado. */
    private static final String KEY_EMAIL = "email";

    /** Clave para guardar la contraseña recordada. */
    private static final String KEY_PASSWORD = "password";

    /** Campo de texto para el correo electrónico. */
    private EditText txtUser;

    /** Campo de texto para la contraseña. */
    private EditText txtPassword;

    /** Checkbox para activar "Recordar credenciales". */
    private CheckBox chkRemember;

    /** Botón de inicio de sesión. */
    private Button btnSign;

    /** Preferencias compartidas para persistir credenciales. */
    private SharedPreferences prefs;

    /** ViewModel de autenticación. */
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Vincular vistas del layout
        txtUser = findViewById(R.id.txtUser);
        txtPassword = findViewById(R.id.txtPassword);
        chkRemember = findViewById(R.id.chkRemember);
        btnSign = findViewById(R.id.btnSign);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView txtOlvide = findViewById(R.id.tvOlvidePassword);

        // Inicializar ViewModel con Hilt
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Cargar credenciales guardadas si existen
        loadSavedCredentials();
        observeViewModel();

        // Click listener para iniciar sesión
        btnSign.setOnClickListener(v -> viewModel.login(
                txtUser.getText().toString(),
                txtPassword.getText().toString()));

        // Navegación a pantalla de registro
        btnRegister.setOnClickListener(v -> Navigator.toRegister(this));

        // Navegación a pantalla de recuperación de contraseña
        txtOlvide.setOnClickListener(v -> Navigator.toForgotPassword(this));
    }

    /**
     * Observa los estados del ViewModel para reaccionar a cambios
     * en la autenticación.
     */
    private void observeViewModel() {
        // Observar estado del login (carga, error)
        viewModel.getLoginState().observe(this, state -> {
            if (state == null) {
                return;
            }
            // Deshabilitar botón durante la carga para evitar doble envío
            btnSign.setEnabled(!state.isLoading());

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearLoginState();
            }
        });

        // Observar evento de login exitoso (un solo consumo)
        viewModel.getLoginSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) {
                return;
            }

            Toast.makeText(this, R.string.success_login, Toast.LENGTH_SHORT).show();

            // Guardar o limpiar credenciales según preferencia del usuario
            String email = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString();
            if (chkRemember.isChecked()) {
                saveCredentials(email, password);
            } else {
                clearSavedCredentials();
            }

            // Navegar al Dashboard principal
            Navigator.toLobby(this);
            finish();
        });
    }

    /**
     * Persiste las credenciales del usuario en SharedPreferences.
     *
     * @param email    Correo electrónico a guardar.
     * @param password Contraseña a guardar.
     */
    private void saveCredentials(String email, String password) {
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    /**
     * Carga las credenciales guardadas previamente y las rellena en los campos.
     */
    private void loadSavedCredentials() {
        String email = prefs.getString(KEY_EMAIL, "");
        String password = prefs.getString(KEY_PASSWORD, "");
        if (!email.isEmpty()) {
            txtUser.setText(email);
            chkRemember.setChecked(true);
        }
        if (!password.isEmpty()) {
            txtPassword.setText(password);
        }
    }

    /**
     * Elimina las credenciales guardadas (cuando el usuario desmarca "Recordar").
     */
    private void clearSavedCredentials() {
        prefs.edit().remove(KEY_EMAIL).remove(KEY_PASSWORD).apply();
    }
}
