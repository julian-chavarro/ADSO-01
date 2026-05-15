package com.example.adso_01.repository;

import androidx.annotation.Nullable;

import com.example.adso_01.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Guarda el perfil del usuario actual en Firestore.
     * Se usa AuthOperationCallback para no exponer tipos de Firebase (Task, OnCompleteListener).
     */
    public void guardarUsuario(Usuario usuario, AuthOperationCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onComplete(false, new Exception("No hay sesión activa"));
            return;
        }
        String userId = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .set(usuario)
                .addOnCompleteListener(task -> 
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }

    /**
     * Obtiene el perfil del usuario actual y lo devuelve como modelo de dominio.
     */
    public void obtenerUsuario(UserProfileCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onResult(null, null);
            return;
        }
        String userId = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    Usuario usuario = mapDocumentToUsuario(doc);
                    callback.onResult(usuario, null);
                })
                .addOnFailureListener(e -> callback.onResult(null, e));
    }

    @Nullable
    private static Usuario mapDocumentToUsuario(DocumentSnapshot doc) {
        if (!doc.exists()) {
            return null;
        }
        
        // Mapeo defensivo con valores por defecto para evitar errores si faltan campos
        int edad = doc.getLong("edad") != null ? doc.getLong("edad").intValue() : 0;
        int peso = doc.getLong("peso") != null ? doc.getLong("peso").intValue() : 0;
        int estatura = doc.getLong("estatura") != null ? doc.getLong("estatura").intValue() : 0;
        int calorias = doc.getLong("calorias") != null ? doc.getLong("calorias").intValue() : 0;
        
        String sexo = doc.getString("sexo") != null ? doc.getString("sexo") : "No definido";
        String actividad = doc.getString("actividad") != null ? doc.getString("actividad") : "No definido";
        String objetivo = doc.getString("objetivo") != null ? doc.getString("objetivo") : "No definido";

        return new Usuario(
                edad,
                peso,
                estatura,
                sexo,
                actividad,
                objetivo,
                calorias
        );
    }
}
