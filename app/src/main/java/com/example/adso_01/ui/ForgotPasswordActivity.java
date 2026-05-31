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
        Button btnBackToLogin = findViewById(R.id.btnVolverLoginForgot);

        viewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(AuthViewModel.class);
        observeViewModel();

        btnSendRecovery.setOnClickListener(v ->
                viewModel.resetPassword(edtEmailForgot.getText().toString()));

        if (btnBackToLogin != null) {
            btnBackToLogin.setOnClickListener(v -> finish());
        }
    }

    private void observeViewModel() {
        viewModel.getResetPasswordState().observe(this, state -> {
            if (state == null) {
                return;
            }
            btnSendRecovery.setEnabled(!state.isLoading());

            if (state.isError() && state.getMessage() != null) {
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_LONG).show();
                viewModel.clearResetPasswordState();
            }
        });

        viewModel.getResetPasswordSuccess().observe(this, event -> {
            if (event == null || event.getContentIfNotHandled() == null) {
                return;
            }
            Toast.makeText(this, R.string.success_reset_password, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
