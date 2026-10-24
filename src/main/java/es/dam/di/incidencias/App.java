package es.dam.di.incidencias;
import java.time.format.DateTimeFormatter;//Este import sirve para poner la fecha en formato español (21/10/2026)


import es.dam.di.incidencias.model.DatosEjemplo;
import es.dam.di.incidencias.model.Estado;
import es.dam.di.incidencias.model.Incidencia;
import es.dam.di.incidencias.model.Prioridad;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación (versión programática).
 *
 * Toda la interfaz se construye con código Java, sin FXML. Para que el
 * código sea fácil de leer, cada zona del wireframe se crea en su propio
 * método:
 *
 *   crearCabecera()      -> zona ① título y botón "Nueva incidencia"
 *   crearIndicadores()   -> zona ② las 4 tarjetas con contadores
 *   crearBarraFiltros()  -> zona ③ búsqueda y filtro por estado
 *   crearZonaCentral()   -> zona ④ tabla + panel de detalle
 *   crearBarraEstado()   -> zona ⑤ mensajes de feedback
 *
 * FASE 2: solo estructura visual. Los botones y filtros todavía no hacen
 * nada; la funcionalidad se añade en la Fase 3.
 */
public class App extends Application {

    // Formato con el que se muestran las fechas: día/mes/año
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // Lista con todas las incidencias (datos en memoria)
    private ObservableList<Incidencia> incidencias;
    // Vista filtrada de la lista: solo las que cumplen la búsqueda y el filtro
    private FilteredList<Incidencia> filtradas;

    // Controles que necesitaremos usar desde otros métodos en la Fase 3.
    // Los declaramos como atributos para poder acceder a ellos.
    private Label lblTotal;
    private Label lblAbiertas;
    private Label lblEnProceso;
    private Label lblResueltas;

    private TextField txtBuscar;
    private ComboBox<String> cmbFiltroEstado;
    private Button btnLimpiar;
    private Button btnNueva;

    private TableView<Incidencia> tabla;

    private Label lblDetalleTitulo;
    private Label lblDetalleCategoria;
    private Label lblDetalleFecha;
    private Label lblDetalleDescripcion;
    private ComboBox<Estado> cmbNuevoEstado;
    private Button btnAplicarEstado;

    private Label lblMensaje;
    private Label lblContador;

    /**
     * start() es el punto de entrada de una aplicación JavaFX.
     * JavaFX lo llama automáticamente y nos pasa el Stage (la ventana).
     */
    @Override
    public void start(Stage stage) {
        incidencias = DatosEjemplo.crearIncidencias();

        // El BorderPane es el contenedor raíz: divide la ventana en zonas.
        BorderPane raiz = new BorderPane();
        raiz.getStyleClass().add("raiz");

        // TOP: cabecera + indicadores + filtros, uno debajo de otro (VBox)
        VBox zonaSuperior = new VBox(crearCabecera(), crearIndicadores(), crearBarraFiltros());
        zonaSuperior.getStyleClass().add("zona-superior");

        raiz.setTop(zonaSuperior);
        raiz.setCenter(crearZonaCentral());
        raiz.setBottom(crearBarraEstado());
        // Una vez creados todos los controles, les asignamos su comportamiento
        configurarEventos();
        //Para poner un listener a la tabla, la tabla tiene que existir ya

        // La Scene es el "contenido" de la ventana. 1100x750 es el tamaño inicial.
        Scene scene = new Scene(raiz, 1100, 750);

        // Cargamos la hoja de estilos desde src/main/resources
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Gestión de incidencias");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    // =====================================================================
    // ① CABECERA
    // =====================================================================
    private HBox crearCabecera() {
        Label titulo = new Label("Gestión de incidencias");
        titulo.getStyleClass().add("titulo-app");

        btnNueva = new Button("+ Nueva incidencia");
        btnNueva.getStyleClass().add("boton-principal");

        // Un Region vacío con Hgrow=ALWAYS ocupa todo el espacio libre
        // y empuja el botón hacia la derecha.
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        HBox cabecera = new HBox(titulo, espaciador, btnNueva);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.getStyleClass().add("cabecera");
        return cabecera;
    }

    // =====================================================================
    // ② INDICADORES
    // =====================================================================
    private HBox crearIndicadores() {
        // Creamos los Label de los números. De momento con el recuento
        // inicial; en la Fase 3 se actualizarán automáticamente.
        lblTotal = new Label(String.valueOf(incidencias.size()));
        lblAbiertas = new Label(String.valueOf(contar(Estado.ABIERTA)));
        lblEnProceso = new Label(String.valueOf(contar(Estado.EN_PROCESO)));
        lblResueltas = new Label(String.valueOf(contar(Estado.RESUELTA)));

        HBox indicadores = new HBox(
            crearTarjeta("▣ TOTAL", lblTotal, "kpi-total"),
            crearTarjeta(Estado.ABIERTA.getSimbolo() + " ABIERTAS", lblAbiertas, "kpi-abiertas"),
            crearTarjeta(Estado.EN_PROCESO.getSimbolo() + " EN PROCESO", lblEnProceso, "kpi-proceso"),
            crearTarjeta(Estado.RESUELTA.getSimbolo() + " RESUELTAS", lblResueltas, "kpi-resueltas")
        );
        indicadores.getStyleClass().add("indicadores");
        return indicadores;
    }

    /**
     * Crea una tarjeta de indicador: etiqueta pequeña arriba y número grande abajo.
     * Reutilizamos este método para las 4 tarjetas, así todas son iguales (consistencia).
     */
    private VBox crearTarjeta(String texto, Label lblValor, String claseColor) {
        Label lblTexto = new Label(texto);
        lblTexto.getStyleClass().add("kpi-texto");
        lblValor.getStyleClass().add("kpi-valor");

        VBox tarjeta = new VBox(lblTexto, lblValor);
        tarjeta.getStyleClass().addAll("kpi-tarjeta", claseColor);

        // Que las 4 tarjetas se repartan el ancho por igual
        HBox.setHgrow(tarjeta, Priority.ALWAYS);
        tarjeta.setMaxWidth(Double.MAX_VALUE);
        return tarjeta;
    }

    /** Cuenta cuántas incidencias hay en un estado concreto. */
    private long contar(Estado estado) {
        return incidencias.stream().filter(i -> i.getEstado() == estado).count();
    }

    // =====================================================================
    // ③ BARRA DE BÚSQUEDA Y FILTRO
    // =====================================================================
    private HBox crearBarraFiltros() {
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por título, descripción o categoría...");
        HBox.setHgrow(txtBuscar, Priority.ALWAYS);   // el buscador se estira

        Label lblEstado = new Label("Estado:");

        cmbFiltroEstado = new ComboBox<>();
        cmbFiltroEstado.getItems().add("Todos");
        for (Estado e : Estado.values()) {
            cmbFiltroEstado.getItems().add(e.toString());
        }
        cmbFiltroEstado.getSelectionModel().selectFirst();   // "Todos" por defecto

        btnLimpiar = new Button("Limpiar filtros");
        btnLimpiar.getStyleClass().add("boton-secundario");

        HBox barra = new HBox(txtBuscar, lblEstado, cmbFiltroEstado, btnLimpiar);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.getStyleClass().add("barra-filtros");
        return barra;
    }

    // =====================================================================
    // ④ ZONA CENTRAL: TABLA + DETALLE
    // =====================================================================
    private SplitPane crearZonaCentral() {
        // SplitPane: dos paneles con una barra en medio que el usuario puede arrastrar
        SplitPane split = new SplitPane(crearTabla(), crearPanelDetalle());
        split.setDividerPositions(0.65);   // la tabla ocupa el 65% al empezar
        split.getStyleClass().add("zona-central");
        return split;
    }

    private TableView<Incidencia> crearTabla() {
        tabla = new TableView<>(incidencias);

        // Cada columna necesita saber QUÉ dato mostrar de cada Incidencia.
        // Eso se indica con setCellValueFactory. ReadOnlyObjectWrapper
        // "envuelve" un valor normal para que la tabla pueda leerlo.
        TableColumn<Incidencia, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colId.setPrefWidth(50);
        colId.setMaxWidth(70);

        TableColumn<Incidencia, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTitulo()));
        colTitulo.setPrefWidth(280);

        TableColumn<Incidencia, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCategoria()));
        colCategoria.setPrefWidth(110);

        TableColumn<Incidencia, Prioridad> colPrioridad = new TableColumn<>("Prioridad");
        colPrioridad.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrioridad()));
        colPrioridad.setPrefWidth(90);

        // El estado es una propiedad JavaFX: se la pasamos directamente,
        // así la celda se actualizará sola cuando cambie (Fase 3).
        TableColumn<Incidencia, Estado> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colEstado.setPrefWidth(120);

        tabla.getColumns().addAll(colId, colTitulo, colCategoria, colPrioridad, colEstado);

        // Las columnas se ajustan al ancho de la tabla (sin barra horizontal)
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(new Label("No hay incidencias que coincidan con la búsqueda"));
        return tabla;
    }

    private VBox crearPanelDetalle() {
        Label titulo = new Label("Detalle de la incidencia");
        titulo.getStyleClass().add("titulo-seccion");

        // Valores del detalle. Mientras no haya selección, mostramos un guion.
        lblDetalleTitulo = new Label("Selecciona una incidencia de la tabla");
        lblDetalleCategoria = new Label("—");
        lblDetalleFecha = new Label("—");
        lblDetalleDescripcion = new Label("—");
        lblDetalleDescripcion.setWrapText(true);   // el texto largo salta de línea
        lblDetalleDescripcion.getStyleClass().add("detalle-descripcion");
        lblDetalleDescripcion.setMaxWidth(Double.MAX_VALUE);

        // Zona para cambiar el estado
        Label lblCambiar = new Label("Cambiar estado");
        lblCambiar.getStyleClass().add("titulo-seccion");

        cmbNuevoEstado = new ComboBox<>();
        cmbNuevoEstado.getItems().addAll(Estado.values());
        cmbNuevoEstado.setMaxWidth(Double.MAX_VALUE);
        cmbNuevoEstado.setPromptText("Elige el nuevo estado");

        btnAplicarEstado = new Button("Aplicar cambio");
        btnAplicarEstado.getStyleClass().add("boton-principal");
        btnAplicarEstado.setMaxWidth(Double.MAX_VALUE);
        btnAplicarEstado.setDisable(true);   // deshabilitado hasta que haya selección

        VBox panel = new VBox(
            titulo,
            new Separator(),
            crearCampoDetalle("Título", lblDetalleTitulo),
            crearCampoDetalle("Categoría · Prioridad", lblDetalleCategoria),
            crearCampoDetalle("Fecha de alta", lblDetalleFecha),
            crearCampoDetalle("Descripción", lblDetalleDescripcion),
            new Separator(Orientation.HORIZONTAL),
            lblCambiar,
            cmbNuevoEstado,
            btnAplicarEstado
        );
        panel.getStyleClass().add("panel-detalle");
        panel.setMinWidth(260);
        return panel;
    }

    /** Crea un bloque "nombre del campo + valor" para el panel de detalle. */
    private VBox crearCampoDetalle(String nombre, Label valor) {
        Label lblNombre = new Label(nombre);
        lblNombre.getStyleClass().add("detalle-nombre");
        valor.getStyleClass().add("detalle-valor");
        VBox campo = new VBox(lblNombre, valor);
        campo.getStyleClass().add("detalle-campo");
        return campo;
    }

    // =====================================================================
    // ⑤ BARRA DE ESTADO
    // =====================================================================
    private HBox crearBarraEstado() {
        lblMensaje = new Label("Listo");
        lblMensaje.getStyleClass().add("mensaje");

        lblContador = new Label("Mostrando " + incidencias.size() + " de " + incidencias.size() + " incidencias");

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        HBox barra = new HBox(lblMensaje, espaciador, lblContador);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.getStyleClass().add("barra-estado");
        return barra;
    }

    /** Método main: lanza la aplicación JavaFX. */
        // =====================================================================
    // EVENTOS (Fase 3)
    // =====================================================================

    /** Asigna a cada control lo que tiene que hacer. */
    private void configurarEventos() {
        // Cuando cambie la fila seleccionada, mostramos su detalle.
        // (observable, anterior, seleccionada) son los 3 datos que nos da JavaFX:
        // la propiedad que cambió, el valor antiguo y el valor nuevo.
        tabla.getSelectionModel().selectedItemProperty().addListener(
            (observable, anterior, seleccionada) -> mostrarDetalle(seleccionada)
        );
           // Al pulsar "Aplicar cambio", cambiamos el estado de la seleccionada
        btnAplicarEstado.setOnAction(evento -> aplicarCambioEstado());
        // Cada vez que cambie la lista (o el estado de alguna incidencia),
        // recalculamos los números de las tarjetas
        incidencias.addListener((ListChangeListener<Incidencia>) cambio -> actualizarIndicadores());
    }

        /** Cambia el estado de la incidencia seleccionada al elegido en el desplegable. */
    private void aplicarCambioEstado() {
        Incidencia inc = tabla.getSelectionModel().getSelectedItem();
        Estado nuevoEstado = cmbNuevoEstado.getValue();

        // Por seguridad: si falta algo, no hacemos nada
        if (inc == null || nuevoEstado == null) {
            return;
        }

        // Si ya tenía ese estado, avisamos en lugar de "cambiar" a lo mismo
        if (inc.getEstado() == nuevoEstado) {
            lblMensaje.setText("La incidencia #" + inc.getId() + " ya estaba en \"" + nuevoEstado.getTexto() + "\"");
            return;
        }

        inc.setEstado(nuevoEstado);

        // Feedback al usuario en la barra de abajo
        lblMensaje.setText("✔ Incidencia #" + inc.getId() + " cambiada a \"" + nuevoEstado.getTexto() + "\"");
    }
        /** Vuelve a contar las incidencias y actualiza los números de las tarjetas. */
    private void actualizarIndicadores() {
        lblTotal.setText(String.valueOf(incidencias.size()));
        lblAbiertas.setText(String.valueOf(contar(Estado.ABIERTA)));
        lblEnProceso.setText(String.valueOf(contar(Estado.EN_PROCESO)));
        lblResueltas.setText(String.valueOf(contar(Estado.RESUELTA)));
    }

    /** Rellena el panel de la derecha con los datos de una incidencia. */
    private void mostrarDetalle(Incidencia inc) {
        // Si no hay ninguna seleccionada (inc == null), dejamos el panel vacío
        if (inc == null) {
            lblDetalleTitulo.setText("Selecciona una incidencia de la tabla");
            lblDetalleCategoria.setText("—");
            lblDetalleFecha.setText("—");
            lblDetalleDescripcion.setText("—");
            cmbNuevoEstado.setValue(null);
            btnAplicarEstado.setDisable(true);
            return;
        }

        lblDetalleTitulo.setText(inc.getTitulo());
        lblDetalleCategoria.setText(inc.getCategoria() + " · " + inc.getPrioridad());
        lblDetalleFecha.setText(inc.getFechaAlta().format(FORMATO_FECHA));
        lblDetalleDescripcion.setText(inc.getDescripcion());

        // El desplegable de "Cambiar estado" empieza mostrando el estado actual
        cmbNuevoEstado.setValue(inc.getEstado());

        // Ya hay una incidencia seleccionada: activamos el botón
        btnAplicarEstado.setDisable(false);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
