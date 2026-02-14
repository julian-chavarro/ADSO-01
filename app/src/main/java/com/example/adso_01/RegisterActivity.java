package com.example.adso_01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtUser, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUser = findViewById(R.id.txtCreateUser);
        txtPassword = findViewById(R.id.txtCreatePassword);
        Button btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(v -> {

            String username = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                        "Complete todos los campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase db = AppDatabase.getInstance(this);

            User existingUser = db.userDao().getUserByUsername(username);

            if (existingUser == null) {

                // 🔥 Creamos usuario usando constructor vacío
                User newUser = new User();
                newUser.username = username;
                newUser.password = password;

                db.userDao().insert(newUser);

                // 🔥 Verificación inmediata
                User testUser = db.userDao().getUserByUsername(username);

                if (testUser != null) {
                    Toast.makeText(this,
                            "Usuario guardado correctamente",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,
                            "ERROR: no se guardó",
                            Toast.LENGTH_LONG).show();
                }

                finish(); // vuelve al login

            } else {
                Toast.makeText(this,
                        "El usuario ya existe",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

