# Registro de uso de IA

Registro de los usos relevantes de herramientas de IA durante el proyecto, según lo pedido en el enunciado.

---

## Entrada 1 — Preparación del entorno

| Campo | Contenido |
|---|---|
| **Fecha** | 24/10/2026 |
| **Problema que queríamos resolver** | Preparar el equipo para el proyecto: qué instalar (Git, Scene Builder, extensiones de VS Code) y cómo crear el repositorio en GitHub. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le pasamos el PDF de la tarea y le preguntamos qué necesitábamos instalar con VS Code y Java 27, enseñándole capturas de los errores de la terminal. |
| **Resumen de la respuesta** | Nos indicó instalar "Extension Pack for Java" en VS Code, Git for Windows y Scene Builder de Gluon. También nos dijo que JavaFX 27 lo descarga Maven automáticamente y nos explicó cómo crear el repositorio en GitHub vacío (sin README) para evitar conflictos. |
| **Qué parte utilizamos** | Todos los pasos de instalación y la creación del repositorio. |
| **Qué modificamos** | Nada. |
| **Cómo validamos la respuesta** | Ejecutamos `mvn -v` y `git --version` en la terminal y comprobamos que respondían con la versión. Comprobamos que el repositorio aparecía en GitHub. |
| **Qué hemos aprendido** | Que la terminal solo detecta un programa recién instalado si se vuelve a abrir. Que hay que configurar `user.name` y `user.email` en Git para que los commits muestren al autor. |

---

## Entrada 2 — Wireframe y decisiones de diseño (Fase 1)

| Campo | Contenido |
|---|---|
| **Fecha** | 24/10/2026 |
| **Problema que queríamos resolver** | Diseñar la distribución de la pantalla antes de programar y justificar las decisiones. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le pedimos que hiciera el wireframe de baja fidelidad y las decisiones de diseño a partir de los requisitos del PDF, explicando el porqué de cada una. |
| **Resumen de la respuesta** | Propuso una ventana con `BorderPane`: indicadores arriba, búsqueda y filtro, tabla con panel de detalle en el centro (`SplitPane`) y barra de estado abajo. El alta de incidencias va en un diálogo modal. Justificó 9 decisiones (jerarquía visual, `TableView` frente a `ListView`, accesibilidad sin depender del color, feedback, CSS...). |
| **Qué parte utilizamos** | _(completar)_ |
| **Qué modificamos** | _(completar: p. ej. columnas, textos, colores...)_ |
| **Cómo validamos la respuesta** | _(completar: p. ej. comprobamos que cada requisito del PDF tiene su sitio en el wireframe)_ |
| **Qué hemos aprendido** | _(completar con vuestras palabras)_ |

---

## Entrada 3 — Proyecto Maven e interfaz programática (Fase 2)

| Campo | Contenido |
|---|---|
| **Fecha** | 24/10/2026 |
| **Problema que queríamos resolver** | Crear el proyecto Maven con JavaFX y construir la estructura visual de la interfaz desde código Java, siguiendo el wireframe, con el aspecto definido en CSS. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le pedimos que creara el proyecto Maven (pom.xml) y la interfaz programática de la Fase 2 a partir del wireframe y las decisiones de diseño, explicando cada parte. |
| **Resumen de la respuesta** | Generó el `pom.xml` con JavaFX 27 y el plugin `javafx-maven-plugin`, el modelo (`Incidencia`, `Estado`, `Prioridad`), 12 incidencias de ejemplo en memoria (`DatosEjemplo`), la clase `App` (un método por zona del wireframe) y `styles.css` con clases de estilo y colores definidos en `.root`. |
| **Qué parte utilizamos** | _(completar)_ |
| **Qué modificamos** | _(completar)_ |
| **Cómo validamos la respuesta** | _(completar: p. ej. ejecutamos `mvn javafx:run`, comparamos la ventana con el wireframe y la redimensionamos para ver que la tabla se estira)_ |
| **Qué hemos aprendido** | _(completar: p. ej. qué es un BorderPane, para qué sirve HBox.setHgrow, cómo se enlaza el CSS con getStyleClass)_ |
