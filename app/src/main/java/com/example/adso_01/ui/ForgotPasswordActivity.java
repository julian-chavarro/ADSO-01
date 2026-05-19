package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.viewmodel.AuthViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmailForgot;
    private Button btnSendRecovery;

    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmailForgot = findViewById(R.id.edtEmailForgot);
        btnSendRecovery = findViewById(R.id.btnSendRecovery);
        Button btnBackToLogin = findViewById(R.id.btnVolverLoginForgot); // Asegúrate de que este ID exista en el XML

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnSendRecovery.setOnClickListener(v -> {
            String email = edtEmailForgot.getText().toString().trim();

            if(email.isEmpty()){
                Toast.makeText(this, "Ingresa tu correo primero", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.resetPassword(email, (success, err) -> {
                if (success) {
                    Toast.makeText(this, "Correo de recuperación enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show();
                    // Al finalizar la actividad, vuelve automáticamente a la pantalla anterior (Login)
                    finish();
                } else {
                    String msg = err != null ? err.getMessage() : "Error desconocido";
                    Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Botón para volver manualmente al login
        if (btnBackToLogin != null) {
            btnBackToLogin.setOnClickListener(v -> finish());
        }
    }
}