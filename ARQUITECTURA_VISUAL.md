# 🏗️ ARQUITECTURA VISUAL - ADSO-01 Fitness App

## Estructura MVVM Correcta

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            USER INTERFACE (VIEW)                        │
│                                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────��───┐                 │
│  │  LoginUI     │  │  RegisterUI  │  │  LobbyUI     │                 │
│  │ .Activity    │  │  .Activity   │  │ .Activity    │                 │
│  │              │  │              │  │              │                 │
│  │ - TextEdit   │  │ - TextEdit   │  │ - Cards      │                 │
│  │ - Button     │  │ - Button     │  │ - BottomNav  │                 │
│  └──────────────┘  └──────────────┘  └──────────────┘                 │
│         │                  │                 │                         │
│         │ Observa          │ Observa         │ Observa                 │
│         └────────────┬──────┴────────────────┘                         │
│                      │                                                  │
└──────────────────────┼──────────────────────────────────────────────────┘
                       │
┌──────��───────────────┼──────────────────────────────────────────────────┐
│                      ▼                                                  │
│              VIEWMODEL LAYER                                            │
│                                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                 │
│  │AuthViewModel │  │UserViewModel │  │RoutineVM     │                 │
│  │              │  │              │  │              │                 │
│  │ LiveData:    │  │ LiveData:    │  │ LiveData:    │                 │
│  │ -loginState  │  │ -userState   │  │ -serieState  │                 │
│  │ -registerSt. │  │ -saveState   │  │              │                 │
│  │              │  │              │  │ Methods:     │                 │
│  │ Methods:     │  │ Methods:     │  │ -saveSerie() │                 │
│  │ -login()     │  │ -loadUser()  │  │ -validate()  │                 │
│  │ -register()  │  │ -saveUser()  │  │              │                 │
│  │ -reset()     │  │ -calcCal()   │  │              │                 │
│  └──────────���───┘  └──────────────┘  └──────────────┘                 │
│         │                  │                 │                         │
│         │ Delega           │ Delega          │ Delega                  │
│         └────────────┬──────┴────────────────┘                         │
│                      │                                                  │
└──────────────────────┼──────────────────────────────────────────────────┘
                       │
┌──────────────────────┼────────────��─────────────────────────────────────┐
│                      ▼                                                  │
│           REPOSITORY / DATA LAYER                                       │
│                                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                 │
│  │AuthRepository│  │UserRepository│  │RoutineRepo   │                 │
│  │              │  │              │  │              │                 │
│  │ Firebase:    │  │ Firebase:    │  │ Firebase:    │                 │
│  │ -login()     │  │ -loadUser()  │  │ -saveSerie() │                 │
│  │ -register()  │  │ -saveUser()  │  │              │                 │
│  │ -reset()     │  │              │  │              │                 │
│  └──────────────┘  └───���──────────┘  └──────────────┘                 │
│         │                  │                 │                         │
│         │ Accede a         │ Accede a        │ Accede a                │
│         └────────────┬──────┴─────��──────────┘                         │
│                      │                                                  │
├──────────────────────┼──────────────────────────────────────────────────┤
│         ┌────────────┴──────────────────┐                              │
│         │   UTIL / ERROR HANDLERS       │                              │
��         │                               │                              │
│  ┌──────────────────────────────────┐  │                              │
│  │FirebaseAuthErrorMapper.toMessage()│  │                              │
│  │FirebaseFirestoreErrorMapper       │  │                              │
���  │     (retorna mensajes amigables)  │  │                              │
│  └─���────────────────────────────────┘  │                              │
│                                         │                              │
└─────────────────────────────────────────┼──────────────────────────────┘
                                           │
                                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│                        FIREBASE BACKEND                                  │
│                                                                          │
│  ┌────────────────┐       ┌──────────────────────────────────────────┐  │
│  │ Authentication │       │      Cloud Firestore Database            │  │
│  │                │       │                                          │  │
│  │ - Users DB     │       │  Collection: usuarios/                  │  │
│  │ - Auth tokens  │       │  ├── {uid}                              │  │
│  │ - Sessions     │       │  │   ├── nombre: String                 │  │
│  │                │       │  │   ├── email: String                  │  │
│  │                │       │  │   ├── edad: int                       │  │
│  │                │       │  │   ├── peso: int                       │  │
│  │                │       │  │   ├── estatura: int                   │  │
│  │                │       │  │   ├── sexo: String                    │  │
│  │                │       │  │   ├── actividad: String               │  │
│  │                │       │  │   ├── objetivo: String                │  │
│  │                │       │  │   ├── calorias: int                   │  │
│  │                │       │  │   └── sesiones/                       │  │
│  │                │       │  │       └── {fecha}/                    │  │
│  │                │       │  │           └── ejercicios/             │  │
│  │                │       │  │               └── {nombre}/           │  │
│  │                │       │  │                   └── series/         │  │
│  │                │       │  │                       └── serie_{n}   │  │
│  │                │       │  │                                       │  │
│  └────────────────┘       └──────────────────────────────────────────┘  │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                        MODEL LAYER (Data Structures)                    │
│                                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                 │
│  │   Usuario    │  │  Ejercicio   │  │   Serie      │                 │
│  │              │  │              │  │              │                 │
│  │ - nombre     │  │ - nombre     │  │ - numero     │                 │
│  │ - email      │  │ - series[]:  │  │ - reps       │                 │
│  │ - edad       │  │   Series     │  │ - peso       │                 │
│  │ - peso       │  │ - grupo      │  │ - completada │                 │
│  │ - estatura   │  │ - imagenUrl  │  │              │                 │
│  │ - sexo       │  │              │  │              │                 │
│  │ - actividad  │  │              │  │              │                 │
│  │ - objetivo   │  │              │  │              │                 │
│  │ - calorias   │  │              │  │              │                 │
│  └──────────────┘  └──────────────┘  └──────────────┘                 │
│                                                                         │
│  ┌──────────────┐  ┌──────────────┐                                   │
│  │FitnessGoals  │  │      Resource │                                  │
│  │(Constantes)  │  │      <T>      │                                  │
│  │              │  │              │                                  │
│  │ GANAR_MUS    │  │ Status.IDLE   │                                  │
│  │ PERDER_GRASA │  │ Status.LOADING│                                  │
│  │ MANTENER     │  │ Status.SUCCESS│                                  │
│  │              │  │ Status.ERROR  │                                  │
│  └──────────────┘  └──────────────┘                                   │
│                                                                         │
│  ┌──────────────┐                                                      │
│  │   Event<T>   │                                                      ���
│  │              │                                                      │
│  │ (One-time    │                                                      │
│  │  consume)    │                                                      │
│  └──────────────┘                                                      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📊 Flujo de Datos: Login

```
User Input (LoginActivity)
    │
    ├─ email, password
    │
    ▼
AuthViewModel.login(email, password)
    │
    ├─ ① Valida isAnyBlank()
    │  └─ Si error → loginState = Resource.error()
    │
    ├─ ✓ OK
    │
    ▼
AuthRepository.login(email, password)
    │
    ├─ Accede a FirebaseAuth.signInWithEmailAndPassword()
    │
    ▼
Firebase (Authentication)
    │
    ├─ Valida credenciales
    │
    ▼
Callback (success: Boolean, error: Exception)
    │
    ├─ Si error
    │  └─ FirebaseAuthErrorMapper.toMessage() → mensaje amigable
    │     └─ loginState = Resource.error(mensaje)
    │
    ├─ Si OK
    │  └─ loginState = Resource.success(null)
    │     └─ loginSuccess = Event(true)
    │
    ▼
LoginActivity observa cambios
    │
    ├─ Desactiva botón si loading
    ├─ Muestra error con Toast
    ├─ Navega a LobbyActivity si success
    │
    ▼
UI Actualizada
```

---

## 📊 Flujo de Datos: Guardar Serie de Ejercicio

```
User completa serie (ExerciseActiveActivity)
    │
    ├─ Ingresa peso, reps
    │
    ▼
RoutineViewModel.saveSerie(nombre, serie, peso)
    │
    ├─ ① validateWeight(peso)
    │  └─ Si error → saveSerieState = Resource.error()
    │
    ├─ ✓ OK
    │
    ├�� ② serie.setPeso(peso)
    │
    ├─ ③ serie.setCompletada(true)
    │
    ┃
    ▼
RoutineRepository.registrarSerie(nombre, serie)
    │
    ├─ Obtiene uid actual
    ├─ Obtiene fecha hoy
    ├─ Sanitiza nombreEjercicio: replaceAll("[^a-zA-Z0-9]", "_")
    │
    ▼
Firebase Firestore WRITE
    │
    ├─ Path: usuarios/{uid}/sesiones/{fecha}/ejercicios/{nombre}/series/serie_{numero}
    │
    ├─ Datos:
    │  ├─ numero
    │  ├─ repeticiones
    │  ├─ peso
    │  ├─ completada
    │  └─ timestamp: serverTimestamp()
    │
    ├─ SetOptions.merge() → No borra datos previos
    │
    ▼
Firebase Response (success: Boolean, error: Exception)
    │
    ├─ Si error
    │  ├─ serie.setCompletada(false) → revertir
    │  └─ saveSerieState = Resource.error(mappedError)
    │
    ├─ Si OK
    │  ├─ saveSerieState = Resource.success(null)
    │  └─ saveSerieSuccess = Event(true)
    │
    ▼
ExerciseActiveActivity observa cambios
    │
    ├─ Muestra success Toast
    ├─ Puede navegar a siguiente serie
    │
    ▼
UI Actualizada
```

---

## 🔄 Patrón LiveData + Event

### Problema que resuelven:

```
Activity rotates (gira pantalla)
    │
    ├─ onCreate() ejecuta de nuevo
    ├─ Subscribers se suscriben de nuevo
    ├─ Eventos se ejecutan MÚLTIPLES VECES
    │  ├─ Toast mostrado 2 veces ❌
    │  ├─ Navegación ocurre 2 veces ❌
    │  └─ Lógica duplicada ❌
```

### Solución implementada:

```
MutableLiveData<Event<Boolean>> loginSuccess

Event<T> {
    content: Boolean = true
    hasBeenHandled: Boolean = false
    
    getContentIfNotHandled() {
        if (hasBeenHandled) return null;
        hasBeenHandled = true;
        return content;
    }
}

Activity observa:
    event?.getContentIfNotHandled()?.let { 
        // Solo ejecuta UNA VEZ
        // Aunque haya rotación de pantalla
    }
```

---

## ✅ Mapeo Correcto de Capas

### Responsabilidades NO BLOQUEADAS ✅

```
MODEL LAYER:
  ✓ Usuarios, Ejercicios, Series
  ✓ Serialización
  ✗ NO: Firebase queries
  ✗ NO: Context
  ✗ NO: UI

VIEW MODEL LAYER:
  ✓ Lógica de negocio
  ✓ Validaciones
  ✓ Estados LiveData
  ✓ Delegación a repository
  ✗ NO: Firebase imports
  ✗ NO: UI updates directo
  ✗ NO: Context directo

REPOSITORY LAYER:
  ✓ Acceso a Firebase
  ✓ Conversión datos
  ✓ Error handling
  ✓ Callbacks
  ✓ Queries/Writes
  ✗ NO: Lógica de negocio
  ✗ NO: Updates UI

VIEW LAYER:
  ✓ Observar LiveData
  ✓ Mostrar datos
  ✓ Capturar input
  ✓ Navegar
  ✗ NO: Firebase imports
  ✗ NO: Queries directas
  ✗ NO: Lógica de negocio
```

---

## 🔴 Problemas de Arquitectura Identificados

### CRÍTICO: RoutineRepository en lugar incorrecto

```
ACTUAL (INCORRECTO):
┌──────────────────────────┐
│ UserProfileCallback.java │
│                          │
│  interface User...Cb {}  │
│                          │
│  class RoutineRepository │  ← ¿Por qué aquí?
│  {                       │
│    registrarSerie()      │
│  }                       │
└──────────────────────────┘
       │
       └─ RoutineViewModel accesa vía:
          UserProfileCallback.RoutineRepository
          └─ Confuso e incorrecto

CORRECTO SERÍA:
┌──────────────────────────┐
│ RoutineRepository.java   │  ← Archivo separado
│                          │
│  class RoutineRepository │
│  {                       │
│    registrarSerie()      │
│  }                       │
└──────────────────────────┘
       │
       └─ RoutineViewModel accesa vía:
          new RoutineRepository()
          └─ Claro y correcto
```

---

## 📈 Flujo de Signup Completo

```
1️⃣ USER INTENTA REGISTRARSE
   │
   ├─ Ingresa: nombre, email, password
   │
   ▼
2️⃣ REGISTER ACTIVITY
   │
   ├─ Lee campos
   ├─ Valida básicamente (trimea strings)
   │
   ▼
3️⃣ AuthViewModel.register(nombre, email, password)
   │
   ├─ Valida isAnyBlank() ✓
   ├─ Valida password.length() >= 6 ✓
   │
   ├─ registerState = Resource.loading()
   │     → Activity desactiva botón CREATE
   │
   ▼
4️⃣ AuthRepository.register()
   │
   ├─ FirebaseAuth.createUserWithEmailAndPassword(email, password)
   │
   ├─ OnSuccess:
   │  └─ Obtiene uid actual
   │     └─ FirebaseFirestore.collection("usuarios").document(uid).set({
   │            "nombre": nombre,
   │            "email": email
   │        })
   │
   ▼
5️⃣ FIREBASE RESPONSES
   │
   ├─ Auth Success + Firestore Success
   │  └─ callback.onComplete(true, null)
   │
   ├─ Auth Success + Firestore Error
   │  └─ callback.onComplete(false, error)
   │
   ├─ Auth Error
   │  └─ callback.onComplete(false, error)
   │     ├─ ERROR_EMAIL_ALREADY_IN_USE
   │     ├─ ERROR_WEAK_PASSWORD
   │     └─ etc...
   │
   ▼
6️⃣ VIEWMODEL RECIBE CALLBACK
   │
   ├─ Si error:
   │  ├─ FirebaseAuthErrorMapper.toMessage(error)
   │  ├─ registerState = Resource.error(mensaje_amigable)
   │
   ├─ Si OK:
   │  ├─ registerState = Resource.success(null)
   │  └─ registerSuccess = Event(true)
   │
   ▼
7️⃣ ACTIVITY OBSERVA CAMBIOS
   │
   ├─ registerState cambia:
   │  ├─ Si loading: desactiva botón
   │  ├─ Si error: Toast + clearState()
   │
   ├─ registerSuccess cambia (después de éxito):
   │  ├─ getContentIfNotHandled() ✓
   │  ├─ Toast: "Cuenta creada!"
   │  ├─ finish() → Vuelve al login
   │
   ▼
8️⃣ USER PUEDE INGRESAR LOGIN

```

---

## 📋 Resumen de Archivos

```
ESTRUCTURA CORRECTA:

com.example.adso_01/
│
├── model/                    ← MODEL LAYER (POJOs)
│   ├── Usuario
│   ├── Ejercicio
│   ├── Serie
│   └── FitnessGoals (Constantes)
│
├── viewmodel/               ← VIEWMODEL LAYER
│   ├── AuthViewModel
│   ├── UserViewModel
│   └── RoutineViewModel
│
├── repository/              ← REPOSITORY LAYER
│   ├── AuthRepository
│   ├── UserRepository
│   ├── RoutineRepository    ← ⚠️ ACTUALMENTE EN UserProfileCallback
│   ├── AuthOperationCallback (interface)
│   ├── UserProfileCallback (interface)
│   ├── RoutineDataSource    ← Datos locales
│   ├── FirebaseAuthErrorMapper
│   └── FirebaseFirestoreErrorMapper
│
├── ui/                      ← VIEW LAYER
│   ├── LoginActivity
│   ├── RegisterActivity
│   ├── ForgotPasswordActivity
│   ├── LobbyActivity
│   ├── ExerciseActiveActivity
│   ├── ExerciseAdapter
│   ├── SetAdapter
│   └── common/
│       ���── Event<T>
│
└── util/                    ← UTILITIES
    ├── Resource<T>
    ├── FirebaseConstants    ← ⚠️ NO EXISTE (recomendar crear)
    └── SharedPreferencesManager ← ⚠️ NO EXISTE (recomendar crear)
```

---

*Diagramas generados por GitHub Copilot - 23 de Mayo 2026*

