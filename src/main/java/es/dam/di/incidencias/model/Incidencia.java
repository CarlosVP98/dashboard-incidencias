package es.dam.di.incidencias.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Una incidencia de la empresa.
 *
 * Casi todos los datos son atributos normales de Java porque no cambian
 * después de crear la incidencia. El estado es distinto: es una
 * "propiedad JavaFX" (ObjectProperty). Una propiedad avisa automáticamente
 * cuando su valor cambia, y así la tabla y los indicadores se podrán
 * actualizar solos cuando cambiemos el estado (lo usaremos en la Fase 3).
 */
public class Incidencia {

    private final int id;
    private final String titulo;
    private final String descripcion;
    private final String categoria;
    private final Prioridad prioridad;
    private final LocalDate fechaAlta;
    private final ObjectProperty<Estado> estado;

    public Incidencia(int id, String titulo, String descripcion, String categoria,
                      Prioridad prioridad, Estado estado, LocalDate fechaAlta) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.fechaAlta = fechaAlta;
        this.estado = new SimpleObjectProperty<>(estado);
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    // --- Estado: getter, setter y la propiedad en sí ---

    public Estado getEstado() {
        return estado.get();
    }

    public void setEstado(Estado nuevoEstado) {
        estado.set(nuevoEstado);
    }

    public ObjectProperty<Estado> estadoProperty() {
        return estado;
    }
}
