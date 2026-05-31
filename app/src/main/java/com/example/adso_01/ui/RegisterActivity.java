package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adso_01.R;
import com.example.adso_01.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtNombre, txtUser, txtPassword;
    private Button btnCreate, btnBackToLogin;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Vincular vistas con el nuevo campo de nombre
        txtNombre = findViewById(R.id.edtNombreRegister);
        txtUser = findViewById(R.id.edtEmailRegister);
        txtPassword = findViewById(R.id.edtPasswordRegister);
        btnCreate = findViewById(R.id.btnCreateAccount);
        btnBackToLogin = findViewById(R.id.btnVolverLoginRegister);

        viewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(AuthViewModel.class);
        
        observeViewModel();

        btnCreate.setOnClickListener(v -> {
            String nombre = txtNombre.getText().toString().trim();
            String email = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            
            viewModel.register(nombre, email, password);
        });

        if (btnBackToLogin != null) {
            btnBackToLogin.setOnClickListener(v -> finish());
        }
    }

    private void observeViewModel() {
        viewModel.getRegisterState().observe(this, state -> {
            if (state == null) return;
            
            btnCreate.setEnabled(!state.isLoading());

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_LONG).show();
                viewModel.clearRegisterState();
            }
        });

        viewModel.getRegisterSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) return;
            
            Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show();
            finish(); // Vuelve al login
        });
    }
}
