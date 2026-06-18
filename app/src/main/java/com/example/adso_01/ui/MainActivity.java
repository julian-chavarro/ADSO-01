package com.example.adso_01.ui;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adso_01.R;
import com.example.adso_01.util.Navigator;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Actividad de inicio (Splash / Landing) de la aplicación.
 * <p>
 * Es la primera pantalla que ve el usuario al abrir la app.
 * Contiene un botón principal que redirige a la pantalla de
 * inicio de sesión ({@link LoginActivity}). Actúa como punto
 * de entrada antes de la autenticación.
 * </p>
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Botón principal que navega a la pantalla de Login
        Button btnStart = findViewById(R.id.btnstart);
        btnStart.setOnClickListener(v -> Navigator.toLogin(this));
    }
}
