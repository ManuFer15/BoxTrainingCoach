# Documentación Técnica del Proyecto: BoxCoach Entrenador (Beta v1.0)

## 1. Visión General del Proyecto
**BoxCoach Entrenador** es una aplicación nativa para Android desarrollada en **Kotlin** con **Jetpack Compose (Material 3)** que actúa como un entrenador virtual de boxeo en casa.

A diferencia de un cronómetro de rounds convencional, la aplicación replica fielmente la estructura real de una clase de gimnasio de boxeo:
- **Generación dinámica según el tiempo disponible** (`30`, `45`, `60`, `75` o `90 minutos`).
- **Estructura común fija de clase:** Calentamiento de **Comba Libre** $\rightarrow$ **Sombra Libre** $\rightarrow$ **Transición de Vendas y Guantes** $\rightarrow$ **Saco Libre de Activación** $\rightarrow$ **Bloque Principal según el Día** $\rightarrow$ **Round Final de Saco Libre**.
- **Sin descansos muertos:** Entre asaltos no hay tiempo vacío; los tramos de **30 segundos de Abdominales** funcionan como **descanso activo** (donde el boxeador hace abdominales o aprovecha para recuperar aire si va muy fatigado), complementado con un botón grande de **Pausa / Reanudar** en cualquier instante.
- **Guía multimodal:** Combina **instrucciones habladas por voz (`TextToSpeech`)**, **campana y silbato de cambio de tramo (`ToneGenerator`)**, **demostración visual animada / soporte de GIFs generados con IA** e **instrucciones escritas desplegables en pantalla**.

---

## 2. Arquitectura del Software (`Clean Architecture + MVVM`)

El proyecto sigue el estándar profesional de desarrollo Android moderno dividido en **4 capas desacopladas**:

```text
BoxTrainig/
├── app/
│   ├── build.gradle.kts                       # Configuración del módulo Android, Compose y dependencias
│   └── src/main/
│       ├── AndroidManifest.xml                # Permisos (VIBRATE, WAKE_LOCK) y configuración responsiva
│       ├── assets/
│       │   └── README_GIFS.md                 # Guía para colocar los archivos .gif generados con IA
│       ├── res/                               # Recursos XML (iconos vectoriales, temas y strings)
│       └── java/com/boxtraining/app/
│           ├── MainActivity.kt                # Punto de entrada, gestión de pantalla activa y navegación raíz
│           ├── core/
│           │   └── audio/
│           │       └── CoachAudioManager.kt   # Motor de voz (TextToSpeech), campana, pitidos y vibración
│           ├── data/
│           │   ├── repository/
│           │   │   └── InMemoryWorkoutRepository.kt # Gestión reactiva (StateFlow) del catálogo y rutina libre
│           │   └── seed/
│           │       └── BoxingCatalogSeed.kt   # Base de datos inicial con +35 ejercicios y combinaciones
│           ├── domain/
│           │   ├── model/
│           │   │   ├── Exercise.kt            # Modelos de Ejercicio, Categorías y Golpes (PunchMove)
│           │   │   ├── WorkoutDayType.kt      # Tipos de Día, Enfoque Muscular, Fases y Roles de Tramo
│           │   │   └── WorkoutSession.kt      # Modelos de Sesión, Round (WorkoutRound) y Tramo (RoundSegment)
│           │   ├── repository/
│           │   │   └── WorkoutRepository.kt   # Interfaz/Contrato del repositorio para inversión de dependencias
│           │   └── usecase/
│           │       └── GenerateWorkoutUseCase.kt # Algoritmo que construye los rounds según tiempo y tipo de día
│           └── ui/
│               ├── components/
│               │   └── ExerciseVisualStage.kt # Visor de GIFs IA + Animación vectorial en vivo + Secuencia de puños
│               ├── screens/
│               │   ├── home/
│               │   │   └── HomeScreen.kt      # Pantalla de configuración (Vertical y Horizontal)
│               │   └── coach/
│               │       └── CoachPlayerScreen.kt # Pantalla "Modo Entrenador" en vivo (Vertical y Horizontal)
│               ├── theme/
│               │   ├── Color.kt               # Paleta oscura de alto contraste y colores semánticos por tramo
│               │   └── Theme.kt               # Configuración de MaterialTheme 3
│               └── viewmodel/
│                   └── BoxingCoachViewModel.kt # Orquestador de estado (CoachUiState) y bucle del cronómetro
├── docs/
│   └── DOCUMENTACION_PROYECTO.md              # Este documento técnico de arquitectura y referencia
├── gradle/
│   └── libs.versions.toml                     # Catálogo centralizado de versiones (Version Catalog)
├── build.gradle.kts                           # Configuración raíz de plugins Gradle
└── settings.gradle.kts                        # Definición de repositorios y módulos del proyecto
```

---

## 3. Explicación Detallada Archivo por Archivo

### 3.1. Capa de Dominio (`com.boxtraining.app.domain`)
Contiene las reglas puras del entrenamiento de boxeo, sin depender de componentes visuales de Android.

| Archivo | ¿Para qué sirve? |
| :--- | :--- |
| [`WorkoutDayType.kt`](../app/src/main/java/com/boxtraining/app/domain/model/WorkoutDayType.kt) | Define los 4 modos de entrenamiento (`TECHNIQUE`, `STRENGTH_BODY`, `STRENGTH_SPEED`, `CUSTOM`), los enfoques musculares (`LOWER_BODY`, `UPPER_BODY`, `FULL_BODY`), las 6 fases de una clase (`WorkoutPhase`) y el rol de cada tramo (`SegmentRole`), indicando cuáles requieren grito de aviso (*"¡YA!"*) y cuáles son descanso activo de abdominales. |
| [`Exercise.kt`](../app/src/main/java/com/boxtraining/app/domain/model/Exercise.kt) | Define la estructura de un ejercicio o combinación (`Exercise`), los golpes individuales (`PunchMove`: Directo Izq `1`, Directo Der `2`, Croché Izq `3`, Croché Der `4`, Gancho Izq `5`, Gancho Der `6`, Gancho al Hígado `3B`, Esquivas y Cintura) y el estilo de animación (`VisualAnimationStyle`). |
| [`WorkoutSession.kt`](../app/src/main/java/com/boxtraining/app/domain/model/WorkoutSession.kt) | Modela la jerarquía temporal de una clase: una `WorkoutSession` contiene una lista de `WorkoutRound`, y cada `WorkoutRound` se divide internamente en uno o varios `RoundSegment` (tramos de tiempo con sus propias instrucciones escritas y habladas). |
| [`WorkoutRepository.kt`](../app/src/main/java/com/boxtraining/app/domain/repository/WorkoutRepository.kt) | Interfaz que desacopla el dominio del origen de datos, definiendo las operaciones para consultar ejercicios, filtrar por categoría y gestionar la rutina libre del usuario. |
| [`GenerateWorkoutUseCase.kt`](../app/src/main/java/com/boxtraining/app/domain/usecase/GenerateWorkoutUseCase.kt) | **El cerebro de la app.** Recibe los minutos disponibles y el tipo de día, calcula cuántos rounds asignar a comba, sombra, saco libre y bloque principal, y ensambla cada round con sus sub-tramos exactos: <br>• **Técnica:** `2:30` de combinación + `0:30` de abdominales.<br>• **Fuerza y Cuerpo:** `3:30` por round dividido en **7 tramos de 30s** (`30s Saco` $\rightarrow$ `30s Ejercicio` $\rightarrow$ `30s Saco` $\rightarrow$ `30s Ejercicio` $\rightarrow$ `30s Abdominales` $\rightarrow$ `30s Saco` $\rightarrow$ `30s Ejercicio`).<br>• **Fuerza y Velocidad:** `2:30` de *"4 Puños + 4 Repeticiones"* + `0:30` de abdominales + rounds de *Hombros y Rodillas*. |

---

### 3.2. Capa de Datos (`com.boxtraining.app.data`)

| Archivo | ¿Para qué sirve? |
| :--- | :--- |
| [`BoxingCatalogSeed.kt`](../app/src/main/java/com/boxtraining/app/data/seed/BoxingCatalogSeed.kt) | Contiene el catálogo inicial completo con más de **35 ejercicios y combinaciones**: calentamiento (comba, sombra, vendas, saco libre), 12 combinaciones técnicas reales de boxeo, 8 ejercicios de tren inferior, 8 de tren superior, 6 variantes de abdominales/descanso activo y 4 variantes del juego de tocar hombros y rodillas. |
| [`InMemoryWorkoutRepository.kt`](../app/src/main/java/com/boxtraining/app/data/repository/InMemoryWorkoutRepository.kt) | Implementación reactiva del repositorio mediante `MutableStateFlow`. Mantiene el catálogo en memoria y permite al usuario seleccionar ejercicios para su **Rutina Libre** o crear nuevas combinaciones propias desde la interfaz. |

---

### 3.3. Capa Core / Audio (`com.boxtraining.app.core.audio`)

| Archivo | ¿Para qué sirve? |
| :--- | :--- |
| [`CoachAudioManager.kt`](../app/src/main/java/com/boxtraining/app/core/audio/CoachAudioManager.kt) | Gestiona toda la respuesta sonora y háptica del entrenador:<br>1. **Locución por voz (`TextToSpeech`)** configurada en español (`es-ES`) para anunciar cada round, cada combinación y cada cambio de 30 segundos.<br>2. **Efectos acústicos (`ToneGenerator`)**: campana al inicio de round, silbato agudo en los cambios de *"¡YA!"*, pitido de aviso a falta de 10 segundos y cuenta atrás `3-2-1`.<br>3. **Vibración (`Vibrator` / `VibratorManager`)** en cada cambio de tramo. |

---

### 3.4. Capa de Interfaz de Usuario (`com.boxtraining.app.ui`)

| Archivo | ¿Para qué sirve? |
| :--- | :--- |
| [`Color.kt`](../app/src/main/java/com/boxtraining/app/ui/theme/Color.kt) y [`Theme.kt`](../app/src/main/java/com/boxtraining/app/ui/theme/Theme.kt) | Definen el tema visual oscuro de gimnasio y los colores semánticos que cambian dinámicamente según lo que toque hacer: **Verde** (Saco/Técnica), **Naranja Fuego** (¡YA! Fuerza 30s), **Dorado** (Velocidad 4x4 y Reacción), **Azul Cian** (Abdominales / Tomar aire) y **Violeta** (Comba/Sombra). |
| [`ExerciseVisualStage.kt`](../app/src/main/java/com/boxtraining/app/ui/components/ExerciseVisualStage.kt) | Componente visual que muestra cómo se realiza el ejercicio:<br>• Si detecta que existe el archivo `.gif` en `app/src/main/assets/`, lo carga y reproduce en bucle infinito con `Coil GifDecoder`.<br>• Si aún no se ha colocado el archivo `.gif`, dibuja en tiempo real una **animación vectorial en Canvas** del ejercicio junto con las **tarjetas de la secuencia de puños iluminándose golpe por golpe**. |
| [`HomeScreen.kt`](../app/src/main/java/com/boxtraining/app/ui/screens/home/HomeScreen.kt) | Pantalla principal de configuración. Permite elegir duración (`30` a `90 min`), tipo de día, enfoque muscular, personalizar rutina libre y desplegar el desglose completo de rounds. Es **100% responsiva** (1 columna en vertical, 2 columnas en horizontal). |
| [`CoachPlayerScreen.kt`](../app/src/main/java/com/boxtraining/app/ui/screens/coach/CoachPlayerScreen.kt) | Pantalla **"Modo Entrenador"**. Muestra el cronómetro gigante, el nombre del ejercicio, la barra de progreso de los sub-tramos del round, el visor animado, un **acordeón desplegable con las pautas paso a paso** (para mantener la vista limpia) y los controles grandes de **Pausa / Reanudar / Saltar tramo**. Se adapta automáticamente a 2 paneles al girar la pantalla en horizontal. |
| [`BoxingCoachViewModel.kt`](../app/src/main/java/com/boxtraining/app/ui/viewmodel/BoxingCoachViewModel.kt) | Mantiene el estado (`CoachUiState`) vivo incluso al girar la pantalla, ejecuta la corrutina del temporizador segundo a segundo y coordina las transiciones automáticas entre tramos y rounds. |
| [`MainActivity.kt`](../app/src/main/java/com/boxtraining/app/MainActivity.kt) | Punto de entrada Android. Activa `FLAG_KEEP_SCREEN_ON` para que la pantalla nunca se bloquee en mitad de un entrenamiento y conecta el `ViewModel` con las pantallas Compose. |

---

## 4. Cómo Añadir tus Propios GIFs Generados con IA

1. Genera tus animaciones en formato `.gif` y guárdalas dentro de la carpeta:
   `app/src/main/assets/`
2. Ponles el nombre indicado en [`app/src/main/assets/README_GIFS.md`](../app/src/main/assets/README_GIFS.md) (por ejemplo, `gif_comba_libre.gif`, `gif_sentadillas.gif`, `gif_combo_1_2_3_6.gif`, etc.).
3. ¡No tienes que tocar código! Al compilar la APK, `ExerciseVisualStage` detectará automáticamente el archivo en `assets/` y mostrará tu GIF en bucle infinito durante ese ejercicio.

---

## 5. Cómo Generar el Archivo `.apk` Instalable para tu Móvil

Desde **Android Studio**:
1. En el menú superior, ve a **Build** $\rightarrow$ **Build Bundle(s) / APK(s)** $\rightarrow$ **Build APK(s)**.
2. El archivo instalable se generará en:
   `app/build/outputs/apk/debug/app-debug.apk`
3. Pásalo a tu móvil Android (por cable USB, WhatsApp, Telegram o Google Drive) e instálalo directamente.
