package es.dam.di.incidencias;
import java.time.LocalDate;
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
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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


    private SplitPane crearZonaCentral() {
        // SplitPane: dos paneles con una barra en medio que el usuario puede arrastrar
        SplitPane split = new SplitPane(crearTabla(), crearPanelDetalle());
        split.setDividerPositions(0.65);   // la tabla ocupa el 65% al empezar
        split.getStyleClass().add("zona-central");
        return split;
    }

    private TableView<Incidencia> crearTabla() {
        // Al principio la condición es "inc -> true": todas pasan el filtro
        filtradas = new FilteredList<>(incidencias, inc -> true);

        // SortedList permite ordenar al pulsar en la cabecera de una columna
        SortedList<Incidencia> ordenadas = new SortedList<>(filtradas);
        tabla = new TableView<>(ordenadas);
        ordenadas.comparatorProperty().bind(tabla.comparatorProperty());

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
        // Al pulsar "+ Nueva incidencia", abrimos el formulario de alta
        btnNueva.setOnAction(evento -> abrirFormularioNueva());
        // Cada vez que cambie la lista (o el estado de alguna incidencia),
        // recalculamos los números de las tarjetas
        incidencias.addListener((ListChangeListener<Incidencia>) cambio -> actualizarIndicadores());
        // Buscar: se filtra mientras se escribe, sin pulsar ningún botón
        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());

        // Filtro por estado: se filtra al elegir otra opción
        cmbFiltroEstado.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());

        // Limpiar: vaciamos el buscador y volvemos a "Todos".
        // Esto dispara los dos listeners de arriba, que ya vuelven a filtrar.
        btnLimpiar.setOnAction(evento -> {
            txtBuscar.clear();
            cmbFiltroEstado.getSelectionModel().selectFirst();
        });

        // El contador de abajo ("Mostrando X de Y") se actualiza cuando
        // cambia la vista filtrada o la lista completa
        filtradas.addListener((ListChangeListener<Incidencia>) cambio -> actualizarContador());
        incidencias.addListener((ListChangeListener<Incidencia>) cambio -> actualizarContador());
    }

    /**
     * Abre el formulario (ventana de diálogo) para crear una incidencia nueva.
     *
     * Un Dialog es una ventana modal: bloquea la ventana principal hasta que
     * se cierra. Es ideal para formularios cortos, porque el usuario no pierde
     * de vista la pantalla principal y no hay que programar otra Scene.
     */
    private void abrirFormularioNueva() {
        Dialog<Incidencia> dialogo = new Dialog<>();
        dialogo.setTitle("Nueva incidencia");
        dialogo.setHeaderText("Rellena los datos de la nueva incidencia");
        dialogo.initOwner(btnNueva.getScene().getWindow());

        // Botones del formulario. OK_DONE y CANCEL_CLOSE le dicen a JavaFX
        // cuál confirma y cuál cancela (y en qué orden colocarlos según el sistema).
        ButtonType tipoCrear = new ButtonType("Crear incidencia", ButtonBar.ButtonData.OK_DONE);
        ButtonType tipoCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getDialogPane().getButtonTypes().addAll(tipoCrear, tipoCancelar);

        // --- Campos del formulario ---
        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Ej.: No funciona la impresora");

        ComboBox<String> cmbCategoria = new ComboBox<>();
        cmbCategoria.getItems().addAll(DatosEjemplo.CATEGORIAS);
        cmbCategoria.setPromptText("Elige una categoría");
        cmbCategoria.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Prioridad> cmbPrioridad = new ComboBox<>();
        cmbPrioridad.getItems().addAll(Prioridad.values());
        cmbPrioridad.setValue(Prioridad.MEDIA);   // valor por defecto razonable
        cmbPrioridad.setMaxWidth(Double.MAX_VALUE);

        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Describe el problema con el mayor detalle posible");
        txtDescripcion.setWrapText(true);
        txtDescripcion.setPrefRowCount(4);

        // Mensaje de error: empieza oculto. Lleva un símbolo (⚠) además del
        // color, para no depender solo del rojo (requisito de accesibilidad).
        Label lblError = new Label();
        lblError.getStyleClass().add("form-error");
        lblError.setWrapText(true);
        lblError.setVisible(false);
        lblError.setManaged(false);   // oculto = no ocupa espacio

        Label lblObligatorio = new Label("* Campo obligatorio");
        lblObligatorio.getStyleClass().add("form-ayuda");

        // --- Distribución: GridPane (filas y columnas) ---
        GridPane rejilla = new GridPane();
        rejilla.getStyleClass().add("form-rejilla");
        rejilla.setPadding(new Insets(10, 0, 0, 0));
        rejilla.addRow(0, new Label("Título *"), txtTitulo);
        rejilla.addRow(1, new Label("Categoría *"), cmbCategoria);
        rejilla.addRow(2, new Label("Prioridad"), cmbPrioridad);
        Label lblDescripcion = new Label("Descripción *");
        GridPane.setValignment(lblDescripcion, VPos.TOP);   // la etiqueta alineada arriba, junto al área de texto
        rejilla.addRow(3, lblDescripcion, txtDescripcion);
        rejilla.add(lblObligatorio, 1, 4);
        rejilla.add(lblError, 0, 5, 2, 1);   // ocupa las 2 columnas

        // La 1ª columna se ajusta al texto; la 2ª se estira con la ventana
        ColumnConstraints colEtiquetas = new ColumnConstraints();
        ColumnConstraints colCampos = new ColumnConstraints();
        colCampos.setHgrow(Priority.ALWAYS);
        rejilla.getColumnConstraints().addAll(colEtiquetas, colCampos);

        dialogo.getDialogPane().setContent(rejilla);
        // El diálogo es otra ventana: hay que darle también la hoja de estilos
        dialogo.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogo.getDialogPane().getStyleClass().add("dialogo-formulario");

        // --- Validación ---
        // Filtramos el clic en "Crear incidencia": si los datos no son válidos,
        // consume() cancela el evento y el diálogo NO se cierra.
        Button botonCrear = (Button) dialogo.getDialogPane().lookupButton(tipoCrear);
        botonCrear.addEventFilter(ActionEvent.ACTION, evento -> {
            String error = validarFormulario(txtTitulo, cmbCategoria, txtDescripcion);
            if (error != null) {
                lblError.setText("⚠ " + error);
                lblError.setVisible(true);
                lblError.setManaged(true);
                evento.consume();
            }
        });

        // Qué devuelve el diálogo al cerrarse: la nueva Incidencia, o null si
        // el usuario canceló. Una incidencia nueva siempre empieza "Abierta"
        // y con la fecha de hoy.
        dialogo.setResultConverter(boton -> {
            if (boton != tipoCrear) {
                return null;
            }
            return new Incidencia(
                siguienteId(),
                txtTitulo.getText().trim(),
                txtDescripcion.getText().trim(),
                cmbCategoria.getValue(),
                cmbPrioridad.getValue(),
                Estado.ABIERTA,
                LocalDate.now());
        });

        // showAndWait() muestra el diálogo y espera. Si hay resultado, la añadimos.
        dialogo.showAndWait().ifPresent(this::anadirIncidencia);
    }

    /** Comprueba los campos obligatorios. Devuelve el mensaje de error, o null si todo está bien. */
    private String validarFormulario(TextField txtTitulo, ComboBox<String> cmbCategoria, TextArea txtDescripcion) {
        if (txtTitulo.getText().isBlank()) {
            txtTitulo.requestFocus();
            return "El título es obligatorio.";
        }
        if (cmbCategoria.getValue() == null) {
            cmbCategoria.requestFocus();
            return "Elige una categoría.";
        }
        if (txtDescripcion.getText().isBlank()) {
            txtDescripcion.requestFocus();
            return "La descripción es obligatoria.";
        }
        return null;
    }

    /** Calcula el id de la siguiente incidencia: el mayor que existe + 1. */
    private int siguienteId() {
        return incidencias.stream().mapToInt(Incidencia::getId).max().orElse(0) + 1;
    }

    /** Añade la incidencia a la lista y avisa al usuario. */
    private void anadirIncidencia(Incidencia nueva) {
        // Al añadirla a la ObservableList, la tabla, los indicadores y el
        // contador se actualizan solos gracias a los listeners de configurarEventos().
        incidencias.add(nueva);

        // Si los filtros activos la esconden, los limpiamos para que el usuario
        // vea la incidencia que acaba de crear.
        if (!filtradas.contains(nueva)) {
            txtBuscar.clear();
            cmbFiltroEstado.getSelectionModel().selectFirst();
        }

        // La seleccionamos: así se ve su detalle y queda marcada en la tabla
        tabla.getSelectionModel().select(nueva);
        tabla.scrollTo(nueva);

        lblMensaje.setText("✔ Incidencia #" + nueva.getId() + " creada: \"" + nueva.getTitulo() + "\"");
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
        /** Cambia la condición de la lista filtrada según el buscador y el filtro de estado. */
    private void aplicarFiltros() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        String filtroEstado = cmbFiltroEstado.getValue();

        // setPredicate recibe la condición que debe cumplir cada incidencia
        // para aparecer en la tabla (true = se muestra, false = se oculta)
        filtradas.setPredicate(inc -> {
            boolean cumpleEstado = filtroEstado == null
                || filtroEstado.equals("Todos")
                || inc.getEstado().toString().equals(filtroEstado);

            boolean cumpleTexto = texto.isEmpty()
                || inc.getTitulo().toLowerCase().contains(texto)
                || inc.getDescripcion().toLowerCase().contains(texto)
                || inc.getCategoria().toLowerCase().contains(texto);

            return cumpleEstado && cumpleTexto;
        });
    }

    /** Actualiza el texto "Mostrando X de Y incidencias" de la barra de estado. */
    private void actualizarContador() {
        lblContador.setText("Mostrando " + filtradas.size() + " de " + incidencias.size() + " incidencias");
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