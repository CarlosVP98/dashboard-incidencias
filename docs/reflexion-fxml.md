# Reflexión sobre FXML y Scene Builder

Proyecto: Dashboard de gestión de incidencias (ABP · UD1 · Desarrollo de Interfaces)

En la Fase 4 pasamos la interfaz de la versión programática (toda en `App.java`) a FXML. El resultado quedó repartido en tres archivos:

- `dashboard.fxml`: la estructura de la ventana.
- `styles.css`: el aspecto (no cambió).
- `DashboardController.java`: el comportamiento.

---

## 1. ¿Qué problema resuelve FXML en una aplicación JavaFX?

En la versión programática, el código que **construye** la ventana (crear un `HBox`, meterle un `Label`, ponerle una clase de CSS…) estaba mezclado con el código que **hace cosas** (filtrar, cambiar el estado, actualizar los contadores). `App.java` tenía unas 600 líneas, y para encontrar algo había que saber en qué método `crear...()` estaba.

FXML separa esas dos cosas: la estructura de la interfaz se describe en un archivo aparte y el código Java solo se ocupa del comportamiento. Así cada archivo tiene una sola responsabilidad, es más fácil de leer y se puede modificar el diseño sin tocar la lógica.

## 2. ¿Qué información contiene un archivo .fxml?

Contiene la **estructura de la interfaz** escrita en XML:

- Los `<?import ...?>` de las clases JavaFX que se usan.
- El **árbol de componentes**: qué contenedores hay y qué tiene cada uno dentro. En nuestro caso, un `BorderPane` con `<top>`, `<center>` y `<bottom>`, y dentro los `HBox`, `VBox`, `SplitPane`, `TableView`, etc.
- Las **propiedades** de cada componente como atributos: `text`, `promptText`, `prefWidth`, `maxWidth`, `disable`, `alignment`…
- Las **clases de CSS** (`styleClass="kpi-tarjeta"`) y la hoja de estilos (`stylesheets="@styles.css"`).
- El **controlador** asociado (`fx:controller="es.dam.di.incidencias.DashboardController"`).
- Los **`fx:id`** de los controles que se usan desde Java y los **`onAction="#metodo"`** de los botones.

Lo que no contiene son los datos ni la lógica: no sabe qué incidencias hay ni cómo se filtran.

## 3. ¿Qué relación existe entre FXML y las clases JavaFX?

Cada etiqueta del FXML corresponde a una **clase de JavaFX**, y cada atributo a una **propiedad** de esa clase. Al cargar el archivo, JavaFX crea los mismos objetos que antes creábamos con `new`.

Un ejemplo de nuestro proyecto. En la versión programática:

```java
btnLimpiar = new Button("Limpiar filtros");
btnLimpiar.getStyleClass().add("boton-secundario");
```

En FXML:

```xml
<Button fx:id="btnLimpiar" styleClass="boton-secundario" text="Limpiar filtros" />
```

Y meter un elemento dentro de otro en el XML equivale a añadirlo como hijo: `new HBox(titulo, espaciador, btnNueva)` es lo mismo que un `<HBox>` con esos tres elementos dentro.

FXML no sustituye a JavaFX: es otra forma de describir los mismos objetos.

## 4. ¿Qué función cumple FXMLLoader?

`FXMLLoader` es la clase que **lee el archivo `.fxml` y crea los objetos** que describe. En nuestra `App.java` es lo único que queda:

```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
Parent raiz = loader.load();
```

Al llamar a `load()`, el loader:

1. Lee el XML y crea todos los componentes (el `BorderPane`, los botones, la tabla…).
2. Crea un objeto de la clase indicada en `fx:controller` (`DashboardController`).
3. Rellena los atributos `@FXML` del controlador con los componentes que tienen el mismo `fx:id`.
4. Enlaza los `onAction="#..."` con los métodos del controlador.
5. Llama al método `initialize()` del controlador.
6. Devuelve el nodo raíz, que luego metemos en la `Scene`.

## 5. ¿Qué utilidad tienen fx:id y la anotación @FXML?

Sirven para **conectar el FXML con el controlador**.

- `fx:id` le da un nombre a un componente del FXML: `<TableView fx:id="tabla">`.
- `@FXML` marca un atributo del controlador para que `FXMLLoader` lo rellene con el componente que tiene ese mismo nombre: `@FXML private TableView<Incidencia> tabla;`.

Sin esto, el controlador no tendría forma de acceder a la tabla para cargarle los datos, ni a los `Label` de los indicadores para cambiar los números. **El nombre tiene que coincidir exactamente**; si no coincide, el atributo se queda en `null`.

`@FXML` también se pone en los métodos que se llaman desde el FXML (`@FXML private void limpiarFiltros()`) y en `initialize()`. Hace falta porque son privados y, sin la anotación, `FXMLLoader` no podría usarlos.

En nuestro FXML hay 22 controles con `fx:id`: solo los que se usan desde Java. Los que no se usan (por ejemplo, los títulos "Detalle de la incidencia" o "Cambiar estado") no lo llevan.

## 6. ¿Qué papel tiene el controlador?

El controlador (`DashboardController`) programa **lo que pasa cuando el usuario interactúa** con la ventana. En nuestro proyecto se encarga de:

- **Cargar los datos** de ejemplo en `initialize()`.
- **Configurar lo que FXML no puede hacer**: decir qué dato muestra cada columna (`setCellValueFactory`), montar la `FilteredList` y la `SortedList`, y rellenar los `ComboBox`.
- **Poner los listeners**: al seleccionar una fila se muestra el detalle; al escribir en el buscador o cambiar el filtro se filtra la tabla; y cuando cambia la lista se actualizan los indicadores y el contador. FXML solo permite enlazar eventos tipo `onAction`, no listeners de propiedades, así que esto tiene que estar en Java.
- **Responder a los botones**: `aplicarCambioEstado()`, `abrirFormularioNueva()` y `limpiarFiltros()`.

Es el equivalente a lo que en la versión programática hacían `configurarEventos()` y los métodos de la Fase 3, pero sin el código que construía la ventana.

## 7. ¿Qué aporta Scene Builder al proceso de diseño?

Permite **ver y editar la interfaz de forma visual** y genera el FXML automáticamente. Lo que hicimos con Scene Builder:

- **Conectar el botón "Limpiar filtros"** con su método: en el panel *Code* escribimos `limpiarFiltros` en *On Action*. Scene Builder añadió `onAction="#limpiarFiltros"` a la línea del botón.
- **Añadir tooltips** a los botones *Aplicar cambio* y *+ Nueva incidencia*: los arrastramos desde la *Library* hasta el botón y escribimos el texto. Scene Builder creó dentro de cada botón un bloque `<tooltip><Tooltip text="..."/></tooltip>` y añadió `<?import javafx.scene.control.Tooltip?>` arriba.
- _(completar con otros cambios que hayáis hecho)_

Lo que más nos aportó:

- **El panel *Hierarchy*** muestra el árbol de componentes, y así se entiende rápido qué está dentro de qué (`BorderPane` → `SplitPane` → `VBox` → `Button`…).
- **Se ve el resultado con el CSS aplicado** sin tener que ejecutar la aplicación cada vez.
- **El panel *Code*** deja claro que el `fx:id` y el `On Action` son la conexión con el controlador.

También vimos algunas limitaciones: Scene Builder **reordena los atributos** al guardar, en la vista previa la tabla y los indicadores salen vacíos (porque los datos los carga el controlador al ejecutar) y algunas propiedades no aparecen donde se esperaría. Por ejemplo, el tooltip no salía en el buscador del *Inspector* y hubo que añadirlo desde la *Library*.

## 8. ¿Qué ha mejorado y qué no ha mejorado en vuestra aplicación después de la refactorización?

### Comparación

| | Versión programática | Versión FXML |
|---|---|---|
| **Estructura de la ventana** | En `App.java`, en los métodos `crear...()` | En `dashboard.fxml` (118 líneas) |
| **Comportamiento** | En `App.java`, mezclado con lo anterior | En `DashboardController.java` (340 líneas) |
| **Clase principal** | `App.java`, unas 600 líneas | `App.java`, 35 líneas: solo carga el FXML |
| **Aspecto visual** | `styles.css` | `styles.css` (sin cambios) |
| **Edición visual** | No | Sí, con Scene Builder |
| **Funcionamiento para el usuario** | — | Idéntico |

### Qué ha mejorado

- **Separación de responsabilidades**: estructura (FXML), aspecto (CSS) y comportamiento (controlador) están en archivos distintos.
- **Es más fácil encontrar las cosas**: para cambiar un texto o un ancho de columna se va al FXML; para cambiar cómo se filtra, al controlador.
- **El diseño se puede editar visualmente** con Scene Builder, sin escribir Java y sin miedo a romper la lógica.
- **El FXML se lee casi como el wireframe**: `<top>`, `<center>` y `<bottom>` coinciden con las zonas que dibujamos.

### Qué no ha mejorado (o ha empeorado)

- **El usuario no nota nada**: la aplicación se ve y funciona igual. La mejora es interna, para quien mantiene el código.
- **Hay más archivos** y hay que mantenerlos **sincronizados**: si se cambia un `fx:id` en el FXML y no en el controlador (o al revés), la app falla al arrancar. En la versión programática, el compilador avisaba de este tipo de errores.
- **No todo puede ir en FXML**: los listeners, las columnas, las listas filtradas y el formulario de nueva incidencia siguen en Java.
- **El formulario de nueva incidencia** se quedó como estaba (construido en Java dentro del controlador). Se podría pasar a su propio FXML, pero al ser un diálogo pequeño que se crea al vuelo, no nos pareció que compensara.
- _(completar con vuestra experiencia: qué os costó más, qué os pareció más cómodo…)_
