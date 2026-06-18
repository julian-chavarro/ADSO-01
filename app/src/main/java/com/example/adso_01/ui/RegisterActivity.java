package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.viewmodel.AuthViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity de registro de nuevo usuario.
 * <p>
 * Permite al usuario crear una cuenta proporcionando nombre,
 * correo electrónico y contraseña. Utiliza {@link AuthViewModel}
 * para realizar el registro mediante Firebase Authentication y
 * crear el perfil en Firestore de forma atómica.
 * Al registrarse exitosamente, vuelve automáticamente a la
 * pantalla de inicio de sesión ({@link LoginActivity}).
 * </p>
 */
@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {

    /** Campo de texto para el nombre completo. */
    private EditText txtNombre;

    /** Campo de texto para el correo electrónico. */
    private EditText txtUser;

    /** Campo de texto para la contraseña. */
    private EditText txtPassword;

    /** Botón para crear la cuenta. */
    private Button btnCreate;

    /** Botón para volver a la pantalla de login. */
    private Button btnBackToLogin;

    /** ViewModel de autenticación. */
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Vincular vistas del layout
        txtNombre = findViewById(R.id.edtNombreRegister);
        txtUser = findViewById(R.id.edtEmailRegister);
        txtPassword = findViewById(R.id.edtPasswordRegister);
        btnCreate = findViewById(R.id.btnCreateAccount);
        btnBackToLogin = findViewById(R.id.btnVolverLoginRegister);

        // Inicializar ViewModel con Hilt
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observeViewModel();

        // Click listener para crear cuenta
        btnCreate.setOnClickListener(v -> {
            String nombre = txtNombre.getText().toString().trim();
            String email = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            viewModel.register(nombre, email, password);
        });

        // Click listener para volver al login
        if (btnBackToLogin != null) {
            btnBackToLogin.setOnClickListener(v -> finish());
        }
    }

    /**
     * Observa los estados del ViewModel para reaccionar al registro.
     */
    private void observeViewModel() {
        // Observar estado del registro (carga, error)
        viewModel.getRegisterState().observe(this, state -> {
            if (state == null) return;

            // Deshabilitar botón durante la carga
            btnCreate.setEnabled(!state.isLoading());

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_LONG).show();
                viewModel.clearRegisterState();
            }
        });

        // Observar evento de registro exitoso (un solo consumo)
        viewModel.getRegisterSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) return;

            Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show();
            finish(); // Vuelve al login
        });
    }
}
