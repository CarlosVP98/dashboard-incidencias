# Registro de uso de IA

Registro de los usos relevantes de herramientas de IA durante el proyecto, según lo pedido en el enunciado.

---

## Entrada 1 — Preparación del entorno

| Campo | Contenido |
|---|---|
| **Fecha** | 21/10/2026 |
| **Problema que queríamos resolver** | Preparar el equipo para el proyecto: qué instalar (Git, Scene Builder, extensiones de VS Code) y cómo crear el repositorio en GitHub. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le pasamos el PDF de la tarea y le preguntamos qué necesitábamos instalar con VS Code y Java 27, enseñándole capturas de los errores de la terminal. |
| **Resumen de la respuesta** | Nos indicó instalar "Extension Pack for Java" en VS Code, que ya lo habíamos hecho en el anteror proyecto de gestión de tareas así que no hizo falta, Git for Windows y Scene Builder de Gluon. También nos dijo que JavaFX 27 lo descarga Maven automáticamente y nos explicó cómo crear el repositorio en GitHub vacío (sin README) para evitar conflictos. |
| **Qué parte utilizamos** | Todos los pasos de instalación y la creación del repositorio. |
| **Qué modificamos** | Nada. |
| **Cómo validamos la respuesta** | Ejecutamos `mvn -v` y `git --version` en la terminal y comprobamos que respondían con la versión. `git --version` falló al principio porque Git no estaba instalado; tras instalarlo y reabrir VS Code ya funcionó. Comprobamos que el repositorio aparecía en GitHub. |
| **Qué hemos aprendido** | Que la terminal solo detecta un programa recién instalado si se vuelve a abrir. Que hay que configurar `user.name` y `user.email` en Git para que los commits muestren al autor (el primer commit falló con "Author identity unknown" hasta que lo configuramos). |

---

## Entrada 2 — Wireframe y decisiones de diseño (Fase 1)

| Campo | Contenido |
|---|---|
| **Fecha** | 21/10/2026 |
| **Problema que queríamos resolver** | Diseñar la distribución de la pantalla antes de programar y justificar las decisiones. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** |Ya que no sabíamos ni se nos ocurría una forma visual para representar el gestor de incidencias, le pedimos nos generase una imagen orientativa del wireframe de baja fidelidad y las decisiones de diseño a partir de los requisitos del PDF, explicando el porqué de cada una. |
| **Resumen de la respuesta** | Propuso una ventana con `BorderPane`: indicadores arriba, búsqueda y filtro, tabla con panel de detalle en el centro (`SplitPane`) y barra de estado abajo. El alta de incidencias va en un diálogo modal. Justificó 9 decisiones (jerarquía visual, `TableView` frente a `ListView`, accesibilidad sin depender del color, feedback, CSS...). |
| **Qué parte utilizamos** | El wireframe completo y las 9 decisiones de diseño. |
| **Qué modificamos** | El wireframe no lo cambiamos antes de programar. Durante la implementación ajustamos el formulario de alta (campos obligatorios, prioridad por defecto, nombre del botón) y lo justificamos en la decisión 7 como "cambio respecto al diseño inicial". También añadimos las decisiones 10 a 13, que surgieron al programar. Al pasar el wireframe a PNG vimos que el recuadro del diálogo tapaba la etiqueta de la zona ⑤ y la movimos. |
| **Cómo validamos la respuesta** | Comprobamos que cada requisito funcional mínimo del PDF (indicadores, listado, detalle, búsqueda, filtro por estado, alta y cambio de estado, feedback) tenía su sitio en el wireframe. Después comparamos la aplicación ya programada con el wireframe. |
| **Qué hemos aprendido** | Que un wireframe de baja fidelidad sirve para decidir *dónde va cada cosa* antes de pensar en colores. Que el `BorderPane` divide la ventana en zonas (top, center, bottom...) y que la zona central es la que crece al redimensionar. Que la información importante no se puede transmitir solo con color, y por eso cada estado lleva un símbolo y su texto. |

---

## Entrada 3 — Proyecto Maven e interfaz programática (Fase 2)

| Campo | Contenido |
|---|---|
| **Fecha** | 24/10/2026 |
| **Problema que queríamos resolver** | Crear el proyecto Maven con JavaFX y construir la estructura visual de la interfaz desde código Java, siguiendo el wireframe, con el aspecto definido en CSS. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le pedimos que creara el proyecto Maven (pom.xml) y la interfaz programática de la Fase 2 a partir del wireframe y las decisiones de diseño, explicando cada parte. |
| **Resumen de la respuesta** | Generó el `pom.xml` con JavaFX 27 y el plugin `javafx-maven-plugin`, el modelo (`Incidencia`, `Estado`, `Prioridad`), 12 incidencias de ejemplo en memoria (`DatosEjemplo`), la clase `App` (un método por zona del wireframe) y `styles.css` con clases de estilo y colores definidos en `.root`. |
| **Qué parte utilizamos** | Todo el proyecto: `pom.xml`, modelo, datos de ejemplo, `App.java`, `styles.css` y `.gitignore`. |
| **Qué modificamos** | El código no lo modificamos. La IA avisó de que no había podido compilarlo en su entorno, así que la primera ejecución en nuestro ordenador fue la prueba real. |
| **Cómo validamos la respuesta** | Ejecutamos `mvn javafx:run` y comprobamos que la ventana coincidía con el wireframe y que, al agrandarla, la tabla se estiraba. Salieron líneas `WARNING` en la terminal: comprobamos que eran avisos de Java sobre JavaFX y no errores. Vimos que los símbolos ▣ y ◐ no se mostraban bien con la fuente de Windows. Comprobamos con `git status` que la carpeta `target` no se subía gracias al `.gitignore`. Por el camino tuvimos que resolver varios problemas: VS Code estaba en *Restricted Mode* y la extensión de Java no cargaba el proyecto, y en la terminal la carpeta es `Documents` aunque en Windows se vea como "Documentos". |
| **Qué hemos aprendido** | Qué es un `BorderPane`, un `HBox` y un `VBox`. Que `HBox.setHgrow(..., Priority.ALWAYS)` hace que un elemento ocupe el espacio sobrante (lo usamos para estirar el buscador y para empujar el botón a la derecha con un `Region` vacío). Que con `getStyleClass().add(...)` se enlaza un nodo con una clase del CSS, de modo que el Java no lleva colores. Que la app hay que lanzarla con `mvn javafx:run` y no con el botón *Run*. Para qué sirven `git add`, `git commit` y `git push`. |

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
| **Qué modificamos** | No cambiamos la lógica propuesta. Al copiar el código a trozos, algunos comentarios quedaron con la sangría descolocada. |
| **Cómo validamos la respuesta** | Probamos cada paso por separado con `mvn javafx:run` antes de pasar al siguiente. Al principio el botón "Aplicar cambio" no hacía nada (la barra seguía diciendo "Listo"): vimos que se nos había olvidado copiar el método `aplicarCambioEstado()`. Comprobamos que al cambiar una incidencia de "Abierta" a "En proceso" el contador de abiertas bajaba y el de en proceso subía, y que el buscador y el filtro reducían las filas y el texto "Mostrando X de 12". |
| **Qué hemos aprendido** | Qué es un *listener* y una lambda; que las propiedades JavaFX avisan cuando cambian y por eso la tabla se actualiza sola; que una lista normal no avisa si cambia algo *dentro* de un elemento y hace falta un extractor; que filtrar no consiste en borrar datos sino en mostrar una "vista" de ellos. También, que un fallo de "no hace nada" suele ser un método o una línea que falta, y que el mensaje de la barra de estado ayuda a ver si el código se está ejecutando. |

---

## Entrada 5 — Formulario de nueva incidencia (Fase 3)

| Campo | Contenido |
|---|---|
| **Fecha** | 24/10/2026 |
| **Problema que queríamos resolver** | Permitir crear una incidencia nueva mediante un formulario, validando los campos obligatorios, y que la tabla, los indicadores y el contador se actualizaran al crearla. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le pedimos que añadiera a nuestra `App.java` el formulario para crear una incidencia nueva, con validación de los campos obligatorios, y que la incidencia creada apareciera en la tabla y actualizara los indicadores. |
| **Resumen de la respuesta** | Propuso un `Dialog<Incidencia>` modal con los botones "Crear incidencia" y "Cancelar" y un `GridPane` con Título, Categoría, Prioridad (por defecto Media) y Descripción. La validación se hace con un `addEventFilter` sobre el botón de crear: si falta un campo obligatorio, se muestra un mensaje con ⚠ y `consume()` evita que el diálogo se cierre. `setResultConverter` construye la incidencia (estado Abierta, fecha de hoy, id = mayor id + 1). Al añadirla, se limpian los filtros si la ocultan y se selecciona en la tabla. Añadió estilos para el diálogo en `styles.css`. |
| **Qué parte utilizamos** | El formulario completo y sus estilos. |
| **Qué modificamos** | Lo integramos en `App.java` junto al resto de la Fase 3 y conectamos el botón "+ Nueva incidencia" con `abrirFormularioNueva()`. Después pedimos a Claude que revisara el código: confirmó que encajaba con los listeners existentes y actualizó la decisión de diseño 7, porque el formulario final pedía más campos obligatorios que el wireframe. |
| **Cómo validamos la respuesta** | Intentamos crear incidencias sin título, sin categoría y sin descripción, y comprobamos que no dejaba y mostraba el aviso. Creamos una válida y comprobamos que aparecía seleccionada en la tabla, que TOTAL y ABIERTAS subían 1 y que el contador pasaba a "Mostrando 13 de 13". |
| **Qué hemos aprendido** | Qué es un diálogo modal y por qué bloquea la ventana principal; que `showAndWait()` muestra el diálogo y espera a que se cierre; que `consume()` cancela un evento (por eso el diálogo no se cierra si hay errores); que `setResultConverter` decide qué devuelve el diálogo según el botón pulsado; y que, como la lista es observable, al añadir la incidencia la tabla y los indicadores se actualizan sin código extra. |

---

## Entrada 6 — Refactorización a FXML (Fase 4)

| Campo | Contenido |
|---|---|
| **Fecha** | 26/10/2026 |
| **Problema que queríamos resolver** | Pasar la interfaz programática a FXML, separando estructura (FXML), presentación (CSS) y comportamiento (controlador). |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le pasamos nuestros apuntes de la Unidad 1 (apartado 13, FXML) y le preguntamos qué podíamos hacer con FXML en el proyecto. Después le pedimos un FXML básico basado en nuestra aplicación. |
| **Resumen de la respuesta** | Relacionó cada método `crear...()` de `App.java` con un bloque del FXML. Generó `dashboard.fxml` (BorderPane con `fx:id` en los controles y `onAction` en los botones), `DashboardController.java` con los atributos `@FXML` y un método `initialize()` para lo que FXML no puede hacer (datos, columnas, listeners), y una `App.java` que solo carga el FXML con `FXMLLoader`. Añadió la dependencia `javafx-fxml` al `pom.xml`. El formulario de nueva incidencia se mantuvo en Java. |
| **Qué parte utilizamos** | `dashboard.fxml`, `DashboardController.java`, la nueva `App.java` y el cambio del `pom.xml`. |
| **Qué modificamos** | Con Scene Builder: (1) conectamos el botón "Limpiar filtros" con `limpiarFiltros` desde el panel *Code* (la IA lo dejó sin conectar a propósito para que lo hiciéramos nosotros), y Scene Builder añadió `onAction="#limpiarFiltros"` a la línea del botón; (2) añadimos tooltips a "Aplicar cambio" y "+ Nueva incidencia" arrastrando un `Tooltip` desde la *Library*, y Scene Builder creó el bloque `<tooltip><Tooltip text="..."/></tooltip>` y el `<?import javafx.scene.control.Tooltip?>`. _(añadir aquí cualquier otro cambio hecho con Scene Builder)_ |
| **Cómo validamos la respuesta** | Ejecutamos con `mvn javafx:run` y comprobamos que todo funcionaba igual que en la versión programática: selección, cambio de estado, indicadores, búsqueda, filtro y alta. Antes de conectarlo, "Limpiar filtros" no hacía nada, y después sí. Abrimos `dashboard.fxml` en VS Code para localizar las líneas que había escrito Scene Builder y vimos que al guardar reordena los atributos. El tooltip no aparecía en el buscador del *Inspector* de nuestra versión de Scene Builder y tuvimos que añadirlo desde la *Library*. |
| **Qué hemos aprendido** | Que FXML describe la estructura de la ventana y el controlador su comportamiento. Que `fx:id` y `@FXML` conectan un control del FXML con un atributo del controlador, y que el nombre debe coincidir exactamente. Que `onAction="#metodo"` sustituye a `setOnAction`, pero que los *listeners* no se pueden poner en FXML y van en `initialize()`, que JavaFX ejecuta después de cargar el FXML. Que la vista previa de Scene Builder muestra la tabla vacía porque los datos se cargan al ejecutar. |

---

## Entrada 7 — Documentación final (Fase 5)

| Campo | Contenido |
|---|---|
| **Fecha** | 26/10/2026 |
| **Problema que queríamos resolver** | Preparar la documentación de entrega: la reflexión sobre FXML, las capturas, el wireframe en PNG, el README y el registro de uso de IA. |
| **Herramienta utilizada** | Claude (Anthropic) |
| **Prompt o consulta realizada** | Le preguntamos cómo se suele hacer un README para GitHub, el formato que suele tenery le pedimos borradores de `reflexion-fxml.md` y del `README.md` basados en nuestro proyecto. También le pedimos que completara este registro con lo que habíamos hecho en cada fase. |
| **Resumen de la respuesta** |Nos explicó que los ficheros de tipo texto suelen ir en formato .md en vez de .txt para que se puedan abrir en el editor de texto y código, en este caso VS Code, y los "caratceres" que se pueden utilizar para que ciertas palabras o secciones del texto se puedan representar de distinta forma (en negrita, en cursiva, separación con lineas entre secciones, introducir el código en tablas, etc...) .Nos explicó las secciones habituales de un README y redactó uno con descripción, funcionalidades, requisitos, cómo ejecutarlo, estructura, tecnologías y autores. Redactó la reflexión respondiendo a las 8 preguntas con ejemplos de nuestro código y una tabla comparando la versión programática con la FXML. Convirtió el wireframe a PNG. |
| **Qué parte utilizamos** | El README, la reflexión y el wireframe en PNG. La captura de la versión programática es nuestra, de cuando la probamos, y la de la versión final la hicimos nosotros. El archivo de IA revisado y refinado por Claude a partir de nuestro archivo original de registros para la presentación.|
| **Qué modificamos** | Completamos en la reflexión los cambios hechos en Scene Builder y nuestra experiencia (preguntas 7 y 8).Le pasamos el archivo de registro de IA que teníamos para documentar las entradas de uso de la misma para que revisase la estructura y nos dijera cómo modificarlo de manera visual para resaltar palabras, separar secciones e introducir los detalles de cada entrada de uso de la IA en distintas tablas, eseparadas en filas y columnas para que visualmente sea más "bonito" En el README añadimos el nombre del compañero y revisamos que los mensajes de commit de la tabla coincidieran con los nuestros (`git log --oneline`). |
| **Cómo validamos la respuesta** | Revisamos el README con la vista previa de VS Code (Ctrl + Shift + V) para comprobar que la captura y los enlaces se veían bien. Comprobamos que la estructura de carpetas del README coincidía con la del proyecto y que las respuestas de la reflexión se correspondían con nuestro código. También comprobamos el documendo de registros de la IA para que reflejase de forma correcta nuestras consultas y mostrase la información de forma clara, concisa y visualmente agradable y profesional.|
| **Qué hemos aprendido** | Qué secciones debe tener un README y que lo más importante es que alguien pueda ejecutar el proyecto sin preguntar nada. Que las imágenes en Markdown se enlazan con ruta relativa para que GitHub las muestre. Y el formato de presentación o escritura en texto literal para su visualización en entronos de desarrollo y páginas web (GitHub).|
