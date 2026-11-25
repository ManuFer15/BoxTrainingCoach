# 🥊 BoxCoach Entrenador — App Personal de Entrenamiento de Boxeo (Android)

Aplicación nativa para Android desarrollada en **Kotlin + Jetpack Compose (Material 3)** bajo arquitectura **Clean Architecture + MVVM**. Diseñada para entrenar boxeo en casa replicando exactamente la estructura, ritmos y dinámicas de las clases reales de gimnasio.

---

## ✨ Características Principales (Versión Beta 1.0)

- **Generador Inteligente por Tiempo (`30`, `45`, `60`, `75` y `90 min`):** Ajusta automáticamente el número de rounds de calentamiento y bloque principal según el tiempo que tengas cada día.
- **Estructura Real de Clase de Boxeo:**
  1. **Calentamiento Común:** Rounds de *Comba Libre* $\rightarrow$ *Sombra Libre* $\rightarrow$ *Ponerse Vendas y Guantes* $\rightarrow$ *Saco Libre de Activación*.
  2. **Bloque Principal según el Tipo de Día:**
     - 🥊 **Día de Técnica:** Combinaciones reales de boxeo rotando round a round con secuencia visual golpe a golpe.
     - 💪 **Día de Fuerza y Cuerpo (Tren Inferior / Tren Superior / Mixto):** Rounds de **`3:30 min`** divididos en **7 tramos continuos de 30 segundos** al grito de *"¡YA!"* (`30s Saco` $\rightarrow$ `30s Ejercicio` $\rightarrow$ `30s Saco` $\rightarrow$ `30s Ejercicio` $\rightarrow$ `30s Abdominales` $\rightarrow$ `30s Saco` $\rightarrow$ `30s Ejercicio`).
     - ⚡ **Día de Fuerza y Velocidad (4x4 + Reacción):** Rounds de **`3:00 min`** (`2:30` de *"4 Puños + 4 Repeticiones"* + últimos `0:30` de *Abdominales*) + rounds del juego de reflejos de *Tocar Hombros y Rodillas*.
     - 🛠️ **Rutina Libre / Personalizada:** Selección libre de ejercicios y creador de nuevas combinaciones dentro de la app.
  3. **Cierre Fijo (Finisher):** 1 round final de `3:00 min` de *Saco Libre* a máxima intensidad.
- **Descanso Activo (Abdominales) + Control Total:** Sin tiempos muertos vacíos; los tramos de 30s de abdominales actúan como recuperación activa para tomar aire, con botón gigante de **Pausar / Reanudar** y salto entre tramos.
- **Entrenador por Voz (`TextToSpeech`) + Instrucciones Desplegables en Pantalla.**
- **Diseño 100% Responsivo (Vertical y Horizontal):** Al girar el móvil en horizontal para apoyarlo frente al saco, la interfaz se divide automáticamente en 2 paneles sin reiniciar el cronómetro.

---

## 📖 Documentación Completa de Arquitectura y Archivos

Toda la explicación técnica detallada del proyecto, capas de arquitectura y función de cada archivo se encuentra en:

👉 **[`docs/DOCUMENTACION_PROYECTO.md`](docs/DOCUMENTACION_PROYECTO.md)**

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Kotlin 2.0 (`JDK 17`)
- **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** Clean Architecture (`domain`, `data`, `core`, `ui`) + MVVM (`AndroidViewModel` + `StateFlow`)
- **Audio y Voz:** Android `TextToSpeech` (`es-ES`), `ToneGenerator` y `VibrationEffect`
- **Multimedia / GIFs IA:** `Coil Compose` + `Coil GifDecoder` (`ImageDecoderDecoder`)
