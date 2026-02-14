package com.example.adso_01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUser, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = findViewById(R.id.txtUser);
        txtPassword = findViewById(R.id.txtPassword);

        Button btnSign = findViewById(R.id.btnSign);
        Button btnRegister = findViewById(R.id.btnRegister); // 🔥 nuevo botón

        // 🔹 BOTÓN INICIAR SESIÓN
        btnSign.setOnClickListener(v -> {

            String username = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                        "Complete todos los campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase db = AppDatabase.getInstance(this);

            List<User> users = db.userDao().getAllUsers();
            Toast.makeText(this,
                    "Usuarios guardados: " + users.size(),
                    Toast.LENGTH_LONG).show();
            User user = db.userDao().login(username, password);

            if (user != null) {

                Toast.makeText(this,
                        "Inicio de sesión correcto",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                startActivity(intent);
                finish();

            } else {

                Toast.makeText(this,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 🔥 BOTÓN REGISTRARSE
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
