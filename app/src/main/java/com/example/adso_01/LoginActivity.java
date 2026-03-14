package com.example.adso_01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUser, txtPassword;
    private CheckBox chkRemember;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "LoginPrefs"; // Nombre del archivo SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Conectar vistas
        txtUser = findViewById(R.id.txtUser);
        txtPassword = findViewById(R.id.txtPassword);
        chkRemember = findViewById(R.id.chkRemember);
        Button btnSign = findViewById(R.id.btnSign);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView txtOlvide = findViewById(R.id.tvOlvidePassword);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();

        // Inicializar SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Cargar datos guardados si existen
        loadSavedCredentials();

        // LOGIN
        btnSign.setOnClickListener(v -> {
            String email = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();

                            // Guardar datos si usuario marcó "Recordarme"
                            if(chkRemember.isChecked()){
                                saveCredentials(email, password);
                            } else {
                                clearCredentials();
                            }

                            // Ir a Lobby
                            Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // REGISTRO
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // OLVIDÉ CONTRASEÑA → Abre nueva Activity
        txtOlvide.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Método para guardar credenciales
    private void saveCredentials(String email, String password){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    // 🔹 Método para cargar credenciales guardadas
    private void loadSavedCredentials(){
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");
        if(!email.isEmpty() && !password.isEmpty()){
            txtUser.setText(email);
            txtPassword.setText(password);
            chkRemember.setChecked(true);
        }
    }

    // 🔹 Método para borrar credenciales
    private void clearCredentials(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}