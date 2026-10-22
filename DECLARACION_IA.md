# Declaración de uso de inteligencia artificial

**Asignatura:** Taller de Nuevas Tecnologías (PA26-04724) · **Evaluación N.º 2** · **Proyecto:** GeoRegistro

## Herramienta utilizada

**Claude** (Anthropic), mediante la aplicación de Claude con acceso a la carpeta del trabajo. 

## Finalidad del uso

- Analizar la guía del estudiante y la rúbrica, y preparar un plan de trabajo paso a paso.
- Explicar conceptos (Activities e Intents, Firestore, reglas de seguridad, permisos en tiempo de ejecución, ubicación, sensores, Google Maps).
- Proponer el código Java y los diseños XML de cada pantalla, que el estudiante integró en Android Studio.
- Ayudar a diagnosticar errores de compilación y de configuración.
- Redactar una bitácora del trabajo y un borrador del README y de esta declaración.

## Ejemplos de consultas

- "Analiza los archivos [guía y rúbrica] y genera un plan de trabajo ordenado paso a paso de cómo se debe realizar."
- "Todas las pantallas deben tener un botón para volver atrás."
- "Ajusta las pantallas y textos."
- "Configurar el uso de Google Maps".

## Qué hizo la IA y qué hizo el estudiante

**Aporte de la IA:** la propuesta de estructura de la aplicación, el código inicial de las clases y los layouts, las reglas de Firestore, explicaciones de cada paso y ayuda para corregir errores.

**Trabajo del estudiante:**
- Creó el proyecto en Android Studio, el repositorio de GitHub y el proyecto de Firebase; descargó e instaló `google-services.json` y activó la autenticación anónima y Firestore.
- Tomó las decisiones de diseño (categorías, fecha automática, acelerómetro con detección de sacudida, listado con RecyclerView, plan gratuito de Firebase sin pagar servicios).
- Pidió agregar botones para volver atrás, y pidió ajustar los márgenes de las pantallas.
- Integró el código en el proyecto y corrigió los problemas que aparecieron (permisos mal ubicados en el Manifest, importaciones faltantes, márgenes que no se aplicaban).
- Probó la aplicación en el emulador en cada etapa: validación del formulario, guardado en Firestore, modo avión, permiso de ubicación concedido y denegado, GPS apagado, historial, mapa y sensor.
- Hizo los commits y subió el proyecto a GitHub, y comprobó que no se subieran credenciales.
- Revisó y estudió el código para poder explicarlo en la interrogación.
- Documentación completa de los pasos a pasos realizados manteniendo un orden y registro de estos.

## Clave de Google Maps

La clave de Maps utilizada es la que el docente facilitó públicamente en clase. **No se incluye en el repositorio**: se guarda en `local.properties` (ignorado por Git) y se inyecta al compilar.

## Datos y credenciales

No se compartieron con la IA datos personales reales ni archivos de credenciales de Firebase.
