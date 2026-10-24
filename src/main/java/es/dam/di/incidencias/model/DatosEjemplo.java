package es.dam.di.incidencias.model;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Datos iniciales en memoria. El enunciado dice que no se usa base de
 * datos: la aplicación arranca siempre con esta lista.
 */
public class DatosEjemplo {

    /** Categorías disponibles (se usarán también en el formulario de alta). */
    public static final String[] CATEGORIAS = {
        "Hardware", "Software", "Red", "Material", "Otros"
    };

    /**
     * Crea la lista de incidencias de ejemplo.
     *
     * Devuelve una ObservableList: una lista que avisa cuando se añaden o
     * quitan elementos. La TableView la escucha y se redibuja sola.
     */
    public static ObservableList<Incidencia> crearIncidencias() {
        LocalDate hoy = LocalDate.now();

        return FXCollections.observableArrayList(
            new Incidencia(1, "No funciona la impresora de recepción",
                "La impresora de recepción muestra un error de atasco aunque no hay papel dentro.",
                "Hardware", Prioridad.ALTA, Estado.ABIERTA, hoy.minusDays(1)),
            new Incidencia(2, "Sin acceso al correo",
                "Desde esta mañana Outlook pide la contraseña continuamente y no deja entrar.",
                "Software", Prioridad.MEDIA, Estado.EN_PROCESO, hoy.minusDays(2)),
            new Incidencia(3, "Pedir ratones nuevos",
                "Se necesitan 5 ratones para el departamento de administración.",
                "Material", Prioridad.BAJA, Estado.RESUELTA, hoy.minusDays(10)),
            new Incidencia(4, "Wifi lenta en la sala de reuniones",
                "La conexión inalámbrica se corta durante las videollamadas.",
                "Red", Prioridad.MEDIA, Estado.ABIERTA, hoy.minusDays(3)),
            new Incidencia(5, "Pantalla parpadea",
                "El monitor del puesto 12 parpadea cada pocos minutos.",
                "Hardware", Prioridad.MEDIA, Estado.EN_PROCESO, hoy.minusDays(4)),
            new Incidencia(6, "Instalar programa de facturación",
                "Instalar la nueva versión del programa de facturación en el equipo de contabilidad.",
                "Software", Prioridad.ALTA, Estado.ABIERTA, hoy);

        );
    }
}
