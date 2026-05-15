package com.example.adso_01.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
     * Registra un usuario y crea su perfil inicial en Firestore de forma atómica.
     */
    public void register(String email, String password, AuthOperationCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email);
                        
                        // Esperamos a que la base de datos se actualice antes de confirmar éxito
                        db.collection("usuarios").document(userId).set(user)
                                .addOnCompleteListener(dbTask -> 
                                    callback.onComplete(dbTask.isSuccessful(), dbTask.getException()));
                    } else {
                        callback.onComplete(false, task.getException());
                    }
                });
    }
}
