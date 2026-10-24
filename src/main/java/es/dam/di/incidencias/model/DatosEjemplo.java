package es.dam.di.incidencias.model;
import javafx.beans.Observable;
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
        ObservableList<Incidencia> lista = FXCollections.observableArrayList(
        incidencia -> new Observable[] { incidencia.estadoProperty() }
        );

        LocalDate hoy = LocalDate.now();

        lista.addAll(
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
                "Software", Prioridad.ALTA, Estado.ABIERTA, hoy),
            new Incidencia(7, "Cambiar tóner",
                "La fotocopiadora de la planta 2 avisa de tóner bajo.",
                "Material", Prioridad.BAJA, Estado.RESUELTA, hoy.minusDays(7)),
            new Incidencia(8, "Equipo muy lento al arrancar",
                "El portátil de dirección tarda más de 5 minutos en encender.",
                "Hardware", Prioridad.MEDIA, Estado.ABIERTA, hoy.minusDays(5)),
            new Incidencia(9, "Crear usuario para nuevo empleado",
                "Dar de alta usuario de dominio y correo para la nueva incorporación del lunes.",
                "Software", Prioridad.ALTA, Estado.EN_PROCESO, hoy.minusDays(1)),
            new Incidencia(10, "Cable de red roto",
                "El cable de red del puesto 4 tiene la pestaña rota y se suelta.",
                "Red", Prioridad.BAJA, Estado.RESUELTA, hoy.minusDays(12)),
            new Incidencia(11, "Silla rota",
                "La silla del puesto 8 no se mantiene a la altura.",
                "Otros", Prioridad.BAJA, Estado.ABIERTA, hoy.minusDays(6)),
            new Incidencia(12, "Copias de seguridad fallan",
                "La copia de seguridad nocturna del servidor termina con error desde hace dos días.",
                "Software", Prioridad.ALTA, Estado.EN_PROCESO, hoy.minusDays(2))
        );
        return lista;
    }
}
