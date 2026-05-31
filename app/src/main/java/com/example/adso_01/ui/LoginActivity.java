package com.example.adso_01.ui;

import android.content.Intent;
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
import com.example.adso_01.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private EditText txtUser, txtPassword;
    private CheckBox chkRemember;
    private Button btnSign;
    private SharedPreferences prefs;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = findViewById(R.id.txtUser);
        txtPassword = findViewById(R.id.txtPassword);
        chkRemember = findViewById(R.id.chkRemember);
        btnSign = findViewById(R.id.btnSign);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView txtOlvide = findViewById(R.id.tvOlvidePassword);

        viewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(AuthViewModel.class);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadSavedCredentials();
        observeViewModel();

        btnSign.setOnClickListener(v -> viewModel.login(
                txtUser.getText().toString(),
                txtPassword.getText().toString()));

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        txtOlvide.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void observeViewModel() {
        viewModel.getLoginState().observe(this, state -> {
            if (state == null) {
                return;
            }
            btnSign.setEnabled(!state.isLoading());

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearLoginState();
            }
        });

        viewModel.getLoginSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) {
                return;
            }

            Toast.makeText(this, R.string.success_login, Toast.LENGTH_SHORT).show();

            String email = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString();
            if (chkRemember.isChecked()) {
                saveCredentials(email, password);
            } else {
                clearSavedCredentials();
            }

            startActivity(new Intent(this, LobbyActivity.class));
            finish();
        });
    }

    private void saveCredentials(String email, String password) {
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

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

    private void clearSavedCredentials() {
        prefs.edit().remove(KEY_EMAIL).remove(KEY_PASSWORD).apply();
    }
}
