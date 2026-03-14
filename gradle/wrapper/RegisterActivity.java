package com.example.adso_01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.ktx.initialize;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtUser, txtPassword;
    private Button btnCreate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔹 Inicializar Firebase antes de usar Auth o Firestore
        Firebase.initialize(this);

        setContentView(R.layout.activity_register);

        // 1️⃣ Conectar vistas
        txtUser = findViewById(R.id.edtEmailRegister);
        txtPassword = findViewById(R.id.edtPasswordRegister);
        btnCreate = findViewById(R.id.btnCreateAccount);

        // 2️⃣ Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 3️⃣ Botón crear usuario
        btnCreate.setOnClickListener(v -> {
            String email = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            // Validaciones básicas
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if(password.length() < 6){
                Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4️⃣ Crear usuario en Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){

                            // Obtener UID del usuario creado
                            String userId = mAuth.getCurrentUser().getUid();

                            // Guardar información adicional en Firestore
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);

                            db.collection("usuarios")
                                    .document(userId)
                                    .set(user)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_LONG).show();
                                        finish(); // vuelve al login
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}