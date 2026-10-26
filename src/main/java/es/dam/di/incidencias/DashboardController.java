package es.dam.di.incidencias;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import es.dam.di.incidencias.model.DatosEjemplo;
import es.dam.di.incidencias.model.Estado;
import es.dam.di.incidencias.model.Incidencia;
import es.dam.di.incidencias.model.Prioridad;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Controlador de dashboard.fxml.
 *
 * El FXML describe cómo es la ventana y este controlador programa lo que
 * pasa cuando el usuario interactúa con ella. Los atributos con @FXML se
 * rellenan solos al cargar el FXML: cada uno corresponde a un control con
 * el mismo fx:id.
 */
public class DashboardController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ObservableList<Incidencia> incidencias;
    private FilteredList<Incidencia> filtradas;

    // Controles del FXML (mismo nombre que su fx:id)
    @FXML private Label lblTotal;
    @FXML private Label lblAbiertas;
    @FXML private Label lblEnProceso;
    @FXML private Label lblResueltas;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private Button btnLimpiar;
    @FXML private Button btnNueva;

    @FXML private TableView<Incidencia> tabla;
    @FXML private TableColumn<Incidencia, Integer> colId;
    @FXML private TableColumn<Incidencia, String> colTitulo;
    @FXML private TableColumn<Incidencia, String> colCategoria;
    @FXML private TableColumn<Incidencia, Prioridad> colPrioridad;
    @FXML private TableColumn<Incidencia, Estado> colEstado;

    @FXML private Label lblDetalleTitulo;
    @FXML private Label lblDetalleCategoria;
    @FXML private Label lblDetalleFecha;
    @FXML private Label lblDetalleDescripcion;
    @FXML private ComboBox<Estado> cmbNuevoEstado;
    @FXML private Button btnAplicarEstado;

    @FXML private Label lblMensaje;
    @FXML private Label lblContador;

    /**
     * JavaFX llama a initialize() automáticamente después de cargar el FXML,
     * cuando todos los controles @FXML ya existen. Aquí va lo que el FXML
     * no puede hacer: cargar los datos, decir qué muestra cada columna y
     * poner los listeners.
     */
    @FXML
    private void initialize() {
        incidencias = DatosEjemplo.crearIncidencias();

        // Opciones de los desplegables
        cmbFiltroEstado.getItems().add("Todos");
        for (Estado e : Estado.values()) {
            cmbFiltroEstado.getItems().add(e.toString());
        }
        cmbFiltroEstado.getSelectionModel().selectFirst();
        cmbNuevoEstado.getItems().addAll(Estado.values());

        configurarTabla();
        configurarEventos();

        actualizarIndicadores();
        actualizarContador();
    }

    private void configurarTabla() {
        // Lista filtrada y ordenable, igual que en la versión programática
        filtradas = new FilteredList<>(incidencias, inc -> true);
        SortedList<Incidencia> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(ordenadas);

        // Qué dato muestra cada columna
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colTitulo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTitulo()));
        colCategoria.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCategoria()));
        colPrioridad.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrioridad()));
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    /**
     * Los botones ya tienen su acción en el FXML (onAction="#..."). Aquí solo
     * quedan los listeners, que no se pueden escribir en FXML.
     */
    private void configurarEventos() {
        tabla.getSelectionModel().selectedItemProperty().addListener(
            (observable, anterior, seleccionada) -> mostrarDetalle(seleccionada)
        );

        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
        cmbFiltroEstado.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());

        incidencias.addListener((ListChangeListener<Incidencia>) cambio -> actualizarIndicadores());
        filtradas.addListener((ListChangeListener<Incidencia>) cambio -> actualizarContador());
        incidencias.addListener((ListChangeListener<Incidencia>) cambio -> actualizarContador());
    }

    // ---------------------------------------------------------------------
    // Acciones de los botones (se enlazan desde el FXML con onAction)
    // ---------------------------------------------------------------------

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cmbFiltroEstado.getSelectionModel().selectFirst();
    }

    @FXML
    private void aplicarCambioEstado() {
        Incidencia inc = tabla.getSelectionModel().getSelectedItem();
        Estado nuevoEstado = cmbNuevoEstado.getValue();

        if (inc == null || nuevoEstado == null) {
            return;
        }

        if (inc.getEstado() == nuevoEstado) {
            lblMensaje.setText("La incidencia #" + inc.getId() + " ya estaba en \"" + nuevoEstado.getTexto() + "\"");
            return;
        }

        inc.setEstado(nuevoEstado);
        lblMensaje.setText("✔ Incidencia #" + inc.getId() + " cambiada a \"" + nuevoEstado.getTexto() + "\"");
    }

    /** Abre el diálogo para crear una incidencia (se mantiene en Java). */
    @FXML
    private void abrirFormularioNueva() {
        Dialog<Incidencia> dialogo = new Dialog<>();
        dialogo.setTitle("Nueva incidencia");
        dialogo.setHeaderText("Rellena los datos de la nueva incidencia");
        dialogo.initOwner(btnNueva.getScene().getWindow());

        ButtonType tipoCrear = new ButtonType("Crear incidencia", ButtonBar.ButtonData.OK_DONE);
        ButtonType tipoCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getDialogPane().getButtonTypes().addAll(tipoCrear, tipoCancelar);

        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Ej.: No funciona la impresora");

        ComboBox<String> cmbCategoria = new ComboBox<>();
        cmbCategoria.getItems().addAll(DatosEjemplo.CATEGORIAS);
        cmbCategoria.setPromptText("Elige una categoría");
        cmbCategoria.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Prioridad> cmbPrioridad = new ComboBox<>();
        cmbPrioridad.getItems().addAll(Prioridad.values());
        cmbPrioridad.setValue(Prioridad.MEDIA);
        cmbPrioridad.setMaxWidth(Double.MAX_VALUE);

        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Describe el problema con el mayor detalle posible");
        txtDescripcion.setWrapText(true);
        txtDescripcion.setPrefRowCount(4);

        Label lblError = new Label();
        lblError.getStyleClass().add("form-error");
        lblError.setWrapText(true);
        lblError.setVisible(false);
        lblError.setManaged(false);

        Label lblObligatorio = new Label("* Campo obligatorio");
        lblObligatorio.getStyleClass().add("form-ayuda");

        GridPane rejilla = new GridPane();
        rejilla.getStyleClass().add("form-rejilla");
        rejilla.setPadding(new Insets(10, 0, 0, 0));
        rejilla.addRow(0, new Label("Título *"), txtTitulo);
        rejilla.addRow(1, new Label("Categoría *"), cmbCategoria);
        rejilla.addRow(2, new Label("Prioridad"), cmbPrioridad);
        Label lblDescripcion = new Label("Descripción *");
        GridPane.setValignment(lblDescripcion, VPos.TOP);
        rejilla.addRow(3, lblDescripcion, txtDescripcion);
        rejilla.add(lblObligatorio, 1, 4);
        rejilla.add(lblError, 0, 5, 2, 1);

        ColumnConstraints colEtiquetas = new ColumnConstraints();
        ColumnConstraints colCampos = new ColumnConstraints();
        colCampos.setHgrow(Priority.ALWAYS);
        rejilla.getColumnConstraints().addAll(colEtiquetas, colCampos);

        dialogo.getDialogPane().setContent(rejilla);
        dialogo.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogo.getDialogPane().getStyleClass().add("dialogo-formulario");

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

        dialogo.showAndWait().ifPresent(this::anadirIncidencia);
    }

    // ---------------------------------------------------------------------
    // Métodos auxiliares
    // ---------------------------------------------------------------------

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

    private int siguienteId() {
        return incidencias.stream().mapToInt(Incidencia::getId).max().orElse(0) + 1;
    }

    private void anadirIncidencia(Incidencia nueva) {
        incidencias.add(nueva);

        if (!filtradas.contains(nueva)) {
            limpiarFiltros();
        }

        tabla.getSelectionModel().select(nueva);
        tabla.scrollTo(nueva);

        lblMensaje.setText("✔ Incidencia #" + nueva.getId() + " creada: \"" + nueva.getTitulo() + "\"");
    }

    private void mostrarDetalle(Incidencia inc) {
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
        cmbNuevoEstado.setValue(inc.getEstado());
        btnAplicarEstado.setDisable(false);
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        String filtroEstado = cmbFiltroEstado.getValue();

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

    private void actualizarIndicadores() {
        lblTotal.setText(String.valueOf(incidencias.size()));
        lblAbiertas.setText(String.valueOf(contar(Estado.ABIERTA)));
        lblEnProceso.setText(String.valueOf(contar(Estado.EN_PROCESO)));
        lblResueltas.setText(String.valueOf(contar(Estado.RESUELTA)));
    }

    private void actualizarContador() {
        lblContador.setText("Mostrando " + filtradas.size() + " de " + incidencias.size() + " incidencias");
    }

    private long contar(Estado estado) {
        return incidencias.stream().filter(i -> i.getEstado() == estado).count();
    }
}
