# 🔧 PLAN DE CORRECCIONES - ADSO-01 Fitness App

## Cambios Requeridos según Análisis de AGENTS.md

---

## 🔴 CAMBIO CRÍTICO #1: Separar RoutineRepository

### Problema
```java
// ACTUAL: En UserProfileCallback.java (línea 31-72)
public interface UserProfileCallback {
    // ... interface methods
    class RoutineRepository { /* 41 líneas de lógica */ }
}
```

### Impacto
- Viola MVVM: anida lógica en una interfaz
- Dificulta testing y mantenimiento
- RoutineViewModel accesa vía `UserProfileCallback.RoutineRepository` (incorrecto)

### Solución
Crear archivo: `app/src/main/java/com/example/adso_01/repository/RoutineRepository.java`

```java
package com.example.adso_01.repository;

import androidx.annotation.Nullable;
import com.example.adso_01.model.Serie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Repositorio encargado de gestionar la persistencia de las rutinas y series 
 * en Cloud Firestore.
 */
public class RoutineRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public RoutineRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Registra una serie completada en Cloud Firestore.
     * Estructura: usuarios/{uid}/sesiones/{fecha}/ejercicios/{nombre_ejercicio}/series/serie_{n}
     */
    public void registrarSerie(String nombreEjercicio, Serie serie, AuthOperationCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onComplete(false, new Exception("Sesión de usuario no encontrada."));
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        // Sanitización del ID del ejercicio para Firestore
        String ejercicioId = nombreEjercicio.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        Map<String, Object> serieMap = new HashMap<>();
        serieMap.put("numero", serie.getNumero());
        serieMap.put("repeticiones", serie.getRepeticiones());
        serieMap.put("peso", serie.getPeso());
        serieMap.put("completada", true);
        serieMap.put("timestamp", FieldValue.serverTimestamp());

        db.collection("usuarios").document(uid)
                .collection("sesiones").document(fechaHoy)
                .collection("ejercicios").document(ejercicioId)
                .collection("series").document("serie_" + serie.getNumero())
                .set(serieMap, SetOptions.merge())
                .addOnCompleteListener(task ->
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }
}
```

### Cambios derivados

**1. Actualizar RoutineViewModel.java**
```java
// ANTES (línea 25):
private final UserProfileCallback.RoutineRepository repository;
repository = new UserProfileCallback.RoutineRepository();

// DESPUÉS:
private final RoutineRepository repository;
repository = new RoutineRepository();

// TAMBIÉN importar:
// import com.example.adso_01.repository.RoutineRepository;
```

**2. Limpiar UserProfileCallback.java**
Remover la clase anidada `RoutineRepository` (líneas 28-72)

---

## 🟡 CAMBIO IMPORTANTE #2: String Hardcodeado en RegisterActivity

### Problema
```java
// RegisterActivity.java línea 67
Toast.makeText(this, "¡Cuenta creada con éxito!", 
              Toast.LENGTH_LONG).show();
```

### Solución

**1. Agregar a `res/values/strings.xml`**
```xml
<string name="success_register">¡Cuenta creada con éxito!</string>
```

**2. Actualizar RegisterActivity.java (línea 67)**
```java
// ANTES:
Toast.makeText(this, "¡Cuenta creada con éxito!", 
              Toast.LENGTH_LONG).show();

// DESPUÉS:
Toast.makeText(this, R.string.success_register, 
              Toast.LENGTH_LONG).show();
```

---

## 🟡 CAMBIO IMPORTANTE #3: Remover Firebase Realtime Database

### Problema
```gradle
// build.gradle.kts línea 62
implementation("com.google.firebase:firebase-database") // NO USADO
```

### Solución
Remover la línea 62 de `build.gradle.kts`:

```gradle
// ANTES:
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
implementation("com.google.firebase:firebase-database") // ← REMOVER ESTO

// DESPUÉS:
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
```

---

## 🟠 MEJORA RECOMENDADA #1: Crear FirebaseConstants.java

### Motivo
Centralizar claves de Firestore para evitar strings hardcodeados

### Archivo Nuevo
`app/src/main/java/com/example/adso_01/util/FirebaseConstants.java`

```java
package com.example.adso_01.util;

/**
 * Constantes para acceso a Cloud Firestore
 * Mantenerlas sincronizadas con reglas de Firestore
 */
public final class FirebaseConstants {

    // Colecciones principales
    public static final String COL_USUARIOS = "usuarios";
    public static final String COL_SESIONES = "sesiones";
    public static final String COL_EJERCICIOS = "ejercicios";
    public static final String COL_SERIES = "series";

    // Campos de Usuario
    public static final String FIELD_NOMBRE = "nombre";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_EDAD = "edad";
    public static final String FIELD_PESO = "peso";
    public static final String FIELD_ESTATURA = "estatura";
    public static final String FIELD_SEXO = "sexo";
    public static final String FIELD_ACTIVIDAD = "actividad";
    public static final String FIELD_OBJETIVO = "objetivo";
    public static final String FIELD_CALORIAS = "calorias";

    // Campos de Serie
    public static final String FIELD_NUMERO = "numero";
    public static final String FIELD_REPETICIONES = "repeticiones";
    public static final String FIELD_PESO_SERIE = "peso";
    public static final String FIELD_COMPLETADA = "completada";
    public static final String FIELD_TIMESTAMP = "timestamp";

    private FirebaseConstants() {
        // No instanciar
    }
}
```

### Cambios derivados - Actualizar Repositories

**AuthRepository.java (línea 44)**
```java
// ANTES:
db.collection("usuarios").document(userId).set(user)

// DESPUÉS:
import com.example.adso_01.util.FirebaseConstants;

db.collection(FirebaseConstants.COL_USUARIOS)
        .document(userId)
        .set(user)
```

**UserRepository.java (linea 34)**
```java
// ANTES:
db.collection("usuarios").document(userId).set(usuario, SetOptions.merge())

// DESPUÉS:
import com.example.adso_01.util.FirebaseConstants;

db.collection(FirebaseConstants.COL_USUARIOS)
        .document(userId)
        .set(usuario, SetOptions.merge())
```

**RoutineRepository.java (línea 64-67)**
```java
// ANTES:
db.collection("usuarios").document(uid)
        .collection("sesiones").document(fechaHoy)
        .collection("ejercicios").document(ejercicioId)
        .collection("series").document("serie_" + serie.getNumero())

// DESPUÉS:
import com.example.adso_01.util.FirebaseConstants;

db.collection(FirebaseConstants.COL_USUARIOS).document(uid)
        .collection(FirebaseConstants.COL_SESIONES).document(fechaHoy)
        .collection(FirebaseConstants.COL_EJERCICIOS).document(ejercicioId)
        .collection(FirebaseConstants.COL_SERIES).document("serie_" + serie.getNumero())
```

---

## 🟠 MEJORA RECOMENDADA #2: Crear SharedPreferencesManager.java

### Motivo
Encapsular lógica de SharedPreferences fuera de Activities

### Archivo Nuevo
`app/src/main/java/com/example/adso_01/util/SharedPreferencesManager.java`

```java
package com.example.adso_01.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Gestor centralizado de SharedPreferences para la aplicación
 */
public class SharedPreferencesManager {

    private static final String PREFS_NAME = "ADSOFitnessPrefs";
    private static final String KEY_REMEMBER_EMAIL = "remember_email";

    private final SharedPreferences prefs;

    public SharedPreferencesManager(@NonNull Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveEmail(@NonNull String email) {
        prefs.edit().putString(KEY_REMEMBER_EMAIL, email).apply();
    }

    public String getSavedEmail() {
        return prefs.getString(KEY_REMEMBER_EMAIL, "");
    }

    public void clearEmail() {
        prefs.edit().remove(KEY_REMEMBER_EMAIL).apply();
    }
}
```

### Cambios derivados - LoginActivity.java

```java
// Agregar campo:
private SharedPreferencesManager prefsManager;

// En onCreate():
prefsManager = new SharedPreferencesManager(this);
// Reemplazar: prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

// Reemplazar métodos:
private void saveEmail(String email) {
    prefsManager.saveEmail(email);
}

private void loadSavedEmail() {
    String email = prefsManager.getSavedEmail();
    if (!email.isEmpty()) {
        txtUser.setText(email);
        chkRemember.setChecked(true);
    }
}

private void clearSavedEmail() {
    prefsManager.clearEmail();
}

// Remover campos innecesarios:
// private static final String PREFS_NAME = ...
// private static final String KEY_EMAIL = ...
// private SharedPreferences prefs;
```

---

## 🟠 MEJORA RECOMENDADA #3: Agregar JavaDoc a Modelos

### Archivos afectados
- `Ejercicio.java` (sin documentación)
- `Serie.java` (sin documentación)

### Actualizar Ejercicio.java (agregar en inicio de clase)

```java
/**
 * Modelo que representa un ejercicio en una rutina.
 * 
 * Contiene la información del ejercicio (nombre, grupos musculares)
 * y una lista de series asociadas a realizar.
 * 
 * Es un POJO puro sin lógica de Firebase ni de UI.
 */
public class Ejercicio implements Serializable { ... }
```

### Actualizar Serie.java (agregar en inicio de clase)

```java
/**
 * Modelo que representa una serie de un ejercicio.
 * 
 * Una serie contiene: número de la serie, repeticiones objetivo,
 * peso utilizado y estado de completación.
 * 
 * Es un POJO puro sin lógica de Firebase ni de UI.
 */
public class Serie implements Serializable { ... }
```

---

## 📋 CHECKLIST DE CAMBIOS

### 🔴 CRÍTICOS (Hacer antes de siguiente PR)
- [ ] Crear `RoutineRepository.java` separado
- [ ] Actualizar import en `RoutineViewModel.java`
- [ ] Limpiar `UserProfileCallback.java`
- [ ] Cambiar string hardcodeado en `RegisterActivity.java`
- [ ] Remover Firebase Realtime Database de `build.gradle.kts`

### 🟡 IMPORTANTES (Próximo Sprint)
- [ ] Crear `FirebaseConstants.java`
- [ ] Actualizar todos los Repositories con constantes
- [ ] Crear `SharedPreferencesManager.java`
- [ ] Actualizar `LoginActivity.java`
- [ ] Agregar JavaDoc a `Ejercicio.java` y `Serie.java`

### 🟠 MEJORAS (Backlog)
- [ ] Implementar Unit Tests
- [ ] Crear clase UseCase para lógica BMR
- [ ] Implementar logging centralizado
- [ ] Completar implementación de `LobbyActivity` campos no usados
- [ ] Considerar RxJava o Coroutines para callbacks async

---

## 🚀 Orden de Ejecución Recomendado

### Semana 1 (Sprint Actual)
1. Separar RoutineRepository
2. Arreglar string hardcodeado
3. Remover Firebase DB del build

### Semana 2
1. Crear FirebaseConstants
2. Actualizar Repositories  
3. Crear SharedPreferencesManager
4. Agregar JavaDoc

### Semana 3+
1. Unit Tests
2. Mejoras architectónicas

---

*Generado por GitHub Copilot - 23 de Mayo 2026*

