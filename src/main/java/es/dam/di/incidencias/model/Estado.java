package es.dam.di.incidencias.model;

/**
 * Estados posibles de una incidencia.
 *
 * Cada estado tiene un texto para mostrar y un símbolo, para no depender
 * solo del color al transmitir la información (requisito de accesibilidad).
 */
public enum Estado {

    ABIERTA("Abierta", "●"),
    EN_PROCESO("En proceso", "◐"),
    RESUELTA("Resuelta", "✔");

    private final String texto;
    private final String simbolo;

    Estado(String texto, String simbolo) {
        this.texto = texto;
        this.simbolo = simbolo;
    }

    public String getTexto() {
        return texto;
    }

    public String getSimbolo() {
        return simbolo;
    }

    /** Lo que se ve en pantalla, por ejemplo: "● Abierta". */
    @Override
    public String toString() {
        return simbolo + " " + texto;
    }
}
