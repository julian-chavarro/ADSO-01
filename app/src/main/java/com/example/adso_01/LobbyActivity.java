
package com.example.adso_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LobbyActivity extends AppCompatActivity {

    Button btnObjets;
    Button btnFood;
    Button btnStartRoutine;
    Button btnChatBubble;

    TextView txtObjetivoUsuario;
    TextView txtCaloriasUsuario;
    LinearLayout cardObjetivo;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // BOTONES
        btnObjets = findViewById(R.id.btnObjets);
        btnFood = findViewById(R.id.btnFood);
        btnStartRoutine = findViewById(R.id.btnStartRoutine);
        btnChatBubble = findViewById(R.id.btnChatBubble);

        // TARJETA OBJETIVOS
        cardObjetivo = findViewById(R.id.cardObjetivo);
        txtObjetivoUsuario = findViewById(R.id.txtObjetivoUsuario);
        txtCaloriasUsuario = findViewById(R.id.txtCaloriasUsuario);

        // FIREBASE
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Cargar objetivos guardados
        cargarObjetivos();

        // ABRIR INTERFAZ OBJETIVOS
        btnObjets.setOnClickListener(v -> {
            Intent intent = new Intent(LobbyActivity.this, ObjetivoActivity.class);
            startActivity(intent);
        });

        // BOTÓN NUTRICIÓN
        btnFood.setOnClickListener(v ->{
            Intent intent = new Intent(LobbyActivity.this, NutritionActivity.class);
            startActivity(intent);
        });

        // BOTÓN RUTINA
        btnStartRoutine.setOnClickListener(v ->
                Toast.makeText(this, "Comenzar rutina", Toast.LENGTH_SHORT).show());

        // CHATBOT
        btnChatBubble.setOnClickListener(v -> {
            Intent intent = new Intent(LobbyActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }

    private void cargarObjetivos(){

        if(auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if(documentSnapshot.exists()){

                        String objetivo = documentSnapshot.getString("objetivo");
                        Long calorias = documentSnapshot.getLong("calorias");

                        // Cambiar texto del botón
                        btnObjets.setText("Editar objetivos");

                        // Mostrar tarjeta
                        cardObjetivo.setVisibility(View.VISIBLE);

                        // Mostrar datos
                        txtObjetivoUsuario.setText("Objetivo: " + objetivo);
                        txtObjetivoUsuario.setTypeface(null, android.graphics.Typeface.BOLD);

                        txtCaloriasUsuario.setText("Calorías recomendadas: " + calorias + " kcal");
                        txtCaloriasUsuario.setTypeface(null, android.graphics.Typeface.BOLD);
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,"Error cargando objetivos",Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarObjetivos();
    }
}

