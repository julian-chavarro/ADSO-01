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
 * Activity para restablecer la contraseña olvidada.
 * <p>
 * Solicita al usuario su correo electrónico registrado y envía
 * un enlace de restablecimiento mediante Firebase Authentication.
 * Incluye un botón para volver a la pantalla de inicio de sesión.
 * </p>
 */
@AndroidEntryPoint
public class ForgotPasswordActivity extends AppCompatActivity {

    /** Campo de texto para el correo electrónico. */
    private EditText edtEmailForgot;

    /** Botón para enviar el correo de recuperación. */
    private Button btnSendRecovery;

    /** ViewModel de autenticación. */
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Vincular vistas del layout
        edtEmailForgot = findViewById(R.id.edtEmailForgot);
        btnSendRecovery = findViewById(R.id.btnSendRecovery);
        Button btnBackToLogin = findViewById(R.id.btnVolverLoginForgot);

        // Inicializar ViewModel con Hilt
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        observeViewModel();

        // Click listener para enviar correo de recuperación
        btnSendRecovery.setOnClickListener(v ->
                viewModel.resetPassword(edtEmailForgot.getText().toString()));

        // Click listener para volver al login
        if (btnBackToLogin != null) {
            btnBackToLogin.setOnClickListener(v -> finish());
        }
    }

    /**
     * Observa los estados del ViewModel para reaccionar al envío
     * del correo de restablecimiento.
     */
    private void observeViewModel() {
        // Observar estado del envío (carga, error)
        viewModel.getResetPasswordState().observe(this, state -> {
            if (state == null) {
                return;
            }
            // Deshabilitar botón durante la carga
            btnSendRecovery.setEnabled(!state.isLoading());

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_LONG).show();
                viewModel.clearResetPasswordState();
            }
        });

        // Observar evento de envío exitoso (un solo consumo)
        viewModel.getResetPasswordSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) {
                return;
            }
            Toast.makeText(this, R.string.success_reset_password, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
