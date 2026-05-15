package com.example.adso_01.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtUser, txtPassword;
    private Button btnCreate;

    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Conectar vistas
        txtUser = findViewById(R.id.edtEmailRegister);
        txtPassword = findViewById(R.id.edtPasswordRegister);
        btnCreate = findViewById(R.id.btnCreateAccount);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Evento del botón
        btnCreate.setOnClickListener(v -> {

            String email = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            // Validaciones
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Registro usando ViewModel
            viewModel.register(email, password, (success, err) -> {

                if (success) {

                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    String msg = err != null ? err.getMessage() : "Error desconocido";
                    Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
                }

            });

        });
    }
}