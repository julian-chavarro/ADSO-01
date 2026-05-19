# AGENTS.md
# Guía de Estándares y Mejores Prácticas
# Proyecto: ADSO-01 Fitness App

Este documento define las reglas técnicas, arquitectónicas y de calidad que deben seguir todos los desarrolladores, asistentes IA o agentes automáticos que trabajen sobre el proyecto ADSO-01 Fitness.

El objetivo principal es garantizar:

- Escalabilidad
- Mantenibilidad
- Código limpio
- Buena experiencia de usuario
- Seguridad
- Rendimiento
- Consistencia arquitectónica

---

# 1. Descripción General del Proyecto

ADSO-01 Fitness es una aplicación móvil Android desarrollada en Android Studio usando Java y arquitectura MVVM.

La aplicación está enfocada en usuarios principiantes del gimnasio y busca brindar:

- Rutinas organizadas
- Seguimiento de progreso
- Gestión de objetivos fitness
- Experiencia intuitiva
- Aprendizaje guiado
- Escalabilidad futura

---

# 2. Stack Tecnológico

## Lenguaje Principal

- Java

## IDE

- Android Studio

## Arquitectura

- MVVM (Model - View - ViewModel)

## Backend

- Firebase

## Servicios Firebase

- Firebase Authentication
- Cloud Firestore
- Firebase Realtime Database

## Control de Versiones

- Git + GitHub

---

# 3. Arquitectura del Sistema (MVVM)

La aplicación debe mantener estrictamente el patrón MVVM.

Separación obligatoria:

## Model (.model)

Responsabilidades:

- Clases de datos
- Objetos de dominio
- DTOs
- Mapeos de datos

Reglas:

- No contener lógica de Firebase
- No contener lógica visual
- Mantener modelos simples y limpios

Ejemplo:

```java
public class User {
    private String uid;
    private String name;
}