package com.example.adso_01.repository;

import androidx.annotation.Nullable;
import com.example.adso_01.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class UserRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Guarda los objetivos del usuario en Firestore.
     * Usa SetOptions.merge() para asegurar que el documento se cree o se actualice
     * sin borrar datos previos como el nombre o el correo electrónico.
     */
    public void guardarUsuario(Usuario usuario, AuthOperationCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onComplete(false, new Exception("Sesión de usuario no válida."));
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // Operación de guardado atómico en la colección 'usuarios'
        db.collection("usuarios")
                .document(userId)
                .set(usuario, SetOptions.merge()) 
                .addOnCompleteListener(task -> 
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }

    /**
     * Obtiene el perfil completo del usuario.
     */
    public void obtenerUsuario(UserProfileCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onResult(null, null);
            return;
        }

        db.collection("usuarios")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> callback.onResult(mapDocumentToUsuario(doc), null))
                .addOnFailureListener(e -> callback.onResult(null, e));
    }

    @Nullable
    private static Usuario mapDocumentToUsuario(DocumentSnapshot doc) {
        if (!doc.exists()) return null;

        Usuario u = new Usuario();
        u.setNombre(doc.getString("nombre")); // Agregado: Mapeo del nombre para el Dashboard
        u.setEmail(doc.getString("email"));
        u.setEdad(doc.getLong("edad") != null ? doc.getLong("edad").intValue() : 0);
        u.setPeso(doc.getLong("peso") != null ? doc.getLong("peso").intValue() : 0);
        u.setEstatura(doc.getLong("estatura") != null ? doc.getLong("estatura").intValue() : 0);
        u.setCalorias(doc.getLong("calorias") != null ? doc.getLong("calorias").intValue() : 0);
        u.setSexo(doc.getString("sexo"));
        u.setActividad(doc.getString("actividad"));
        u.setObjetivo(doc.getString("objetivo"));
        return u;
    }
}
