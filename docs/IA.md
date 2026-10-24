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

---

## Entrada 4 — Funcionalidad: selección, cambio de estado, indicadores y filtros (Fase 3)

| Campo | Contenido |
|---|---|
| **Fecha** | 24/10/2026 |
| **Problema que queríamos resolver** | Hacer que la interfaz de la Fase 2 funcionara: ver el detalle de la incidencia seleccionada, cambiar su estado, que los indicadores se actualizaran solos y poder buscar por texto y filtrar por estado. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le pedimos que nos guiara paso a paso, una funcionalidad cada vez, diciéndonos qué código añadir en `App.java` y `DatosEjemplo.java` y explicando qué hace cada parte. |
| **Resumen de la respuesta** | Nos explicó y nos dio el código para: (1) un *listener* sobre `selectedItemProperty()` de la tabla que rellena el panel de detalle; (2) `setOnAction` en el botón "Aplicar cambio" que cambia la propiedad `estado`; (3) una `ObservableList` con *extractor* que avisa cuando cambia el estado de una incidencia, y un `ListChangeListener` que recalcula los indicadores; (4) una `FilteredList` con `setPredicate` para la búsqueda y el filtro, envuelta en una `SortedList` para conservar la ordenación por columnas. |
| **Qué parte utilizamos** | Todo, escribiéndolo nosotros en el código paso a paso. |
| **Qué modificamos** | _(completar)_ |
| **Cómo validamos la respuesta** | Probamos cada paso por separado con `mvn javafx:run` antes de pasar al siguiente. Al principio el botón "Aplicar cambio" no hacía nada (la barra seguía diciendo "Listo"): vimos que se nos había olvidado copiar el método `aplicarCambioEstado()`. Comprobamos que al cambiar una incidencia de "Abierta" a "En proceso" el contador de abiertas bajaba y el de en proceso subía, y que el buscador y el filtro reducían las filas y el texto "Mostrando X de 12". |
| **Qué hemos aprendido** | Qué es un *listener* y una lambda; que las propiedades JavaFX avisan cuando cambian y por eso la tabla se actualiza sola; que una lista normal no avisa si cambia algo *dentro* de un elemento y hace falta un extractor; que filtrar no consiste en borrar datos sino en mostrar una "vista" de ellos. _(ampliar con vuestras palabras)_ |

---

## Entrada 5 — Formulario de nueva incidencia (Fase 3)


| Campo | Contenido |
|---|---|
| **Fecha** | 24/10/2026 |
| **Problema que queríamos resolver** | Permitir crear una incidencia nueva mediante un formulario, validando los campos obligatorios, y que la tabla, los indicadores y el contador se actualizaran al crearla. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | ¿Cómo puedo hacer una ventana emergente o diálogo en JavaFX para añadir una incidencia nueva? Necesito que pida título, categoría, prioridad y descripción, que compruebe que no estén vacíos antes de cerrarse y que al guardarla se actualice la tabla y los contadores de la pantalla principal. |
| **Resumen de la respuesta** | Propuso un `Dialog<Incidencia>` modal con los botones "Crear incidencia" y "Cancelar" y un `GridPane` con Título, Categoría, Prioridad (por defecto Media) y Descripción. La validación se hace con un `addEventFilter` sobre el botón de crear: si falta un campo obligatorio, se muestra un mensaje con ⚠ y `consume()` evita que el diálogo se cierre. `setResultConverter` construye la incidencia (estado Abierta, fecha de hoy, id = mayor id + 1). Al añadirla, se limpian los filtros si la ocultan y se selecciona en la tabla. Añadió estilos para el diálogo en `styles.css`. |
| **Qué parte utilizamos** | La estructura básica de la clase Dialog con un contenedor tipo GridPane para colocar los campos de texto y los desplegables de manera ordenada. |
| **Qué modificamos** | Cambiamos los nombres de los campos y las opciones de los ComboBox (categorías y prioridades) para que encajaran exactamente con los nombres de las variables y enumerados que ya teníamos creados en nuestro proyecto. |
| **Cómo validamos la respuesta** | Probamos a darle al botón de crear dejando campos vacíos para comprobar que saltaba el aviso y la ventana no se cerraba.
Después, rellenamos todo bien, le dimos a aceptar y comprobamos que la incidencia aparecía seleccionada en la tabla, que los contadores de "Total" y "Abiertas" subían uno, y que el texto inferior indicaba correctamente el nuevo total de elementos.|
| **Qué hemos aprendido** | A ver cómo funcionan los diálogos modales y para qué sirve bloquear la pantalla con showAndWait().
A usar consume() dentro de un filtro de eventos para frenar el cierre de la ventana si la validación de los campos obligatorios falla. |
