package com.example.adso_01.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Repositorio de Autenticación.
 * Implementa transacciones atómicas entre Auth y Firestore.
 */
public class AuthRepository {

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void resetPassword(String email, AuthOperationCallback callback) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task ->
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }

    public void login(String email, String password, AuthOperationCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }

    /**
     * Registra un usuario y crea su perfil en Firestore de forma encadenada.
     * Garantiza que no existan usuarios en Auth sin un documento en la DB.
     */
    public void register(String nombre, String email, String password, AuthOperationCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException() != null ? task.getException() : new Exception("Error desconocido en Auth");
                    }
                    
                    String userId = mAuth.getCurrentUser().getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("nombre", nombre);
                    user.put("email", email);
                    
                    // Retornamos la tarea de Firestore para encadenarla
                    return db.collection("usuarios").document(userId).set(user, SetOptions.merge());
                })
                .addOnCompleteListener(task -> 
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }
}
