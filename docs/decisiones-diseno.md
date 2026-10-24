# Decisiones de diseño

Proyecto: Dashboard de gestión de incidencias (ABP · UD1 · Desarrollo de Interfaces)

Este documento recoge las decisiones de diseño tomadas antes de programar (Fase 1) y se irá ampliando si alguna cambia durante la implementación.

---

## 1. Estructura general con `BorderPane`

**Decisión:** la ventana principal usa un `BorderPane` como contenedor raíz:

- **Top:** cabecera, indicadores y barra de búsqueda/filtro (agrupados en un `VBox`).
- **Center:** tabla de incidencias y panel de detalle.
- **Bottom:** barra de estado con mensajes de feedback.

**Justificación:** el `BorderPane` divide la pantalla en zonas con un significado claro. Además, la zona central se estira automáticamente al redimensionar la ventana, que es justo lo que queremos para la tabla (la parte con más información). Así evitamos posiciones fijas con coordenadas.

## 2. Indicadores arriba, en cuatro tarjetas del mismo tamaño

**Decisión:** los cuatro indicadores (Total, Abiertas, En proceso, Resueltas) van justo debajo del título, en un `HBox` con cuatro tarjetas iguales. Cada tarjeta muestra el número en grande y la etiqueta en pequeño.

**Justificación:** el enunciado pide que la dirección conozca el estado *de un vistazo*. Lo primero que se lee en una pantalla es la parte superior izquierda, así que ahí colocamos la información más importante. El número grande crea jerarquía visual: se ve antes la cifra que la etiqueta. Las tarjetas tienen el mismo ancho (`HBox.setHgrow`) para mantener la consistencia.

## 3. `TableView` para el listado en lugar de `ListView`

**Decisión:** el listado se muestra en un `TableView` con columnas ID, Título, Categoría, Prioridad y Estado.

**Justificación:** cada incidencia tiene varios datos que el usuario quiere comparar entre filas (por ejemplo, ver todas las de prioridad alta). Una tabla organiza esos datos en columnas alineadas y permite ordenar pulsando la cabecera. Un `ListView` solo muestra una línea por elemento y obligaría a juntar todo en un texto.

## 4. Panel de detalle a la derecha de la tabla (vista maestro-detalle)

**Decisión:** al seleccionar una fila, sus datos completos (incluida la descripción larga) aparecen en un panel a la derecha. En ese mismo panel está el control para cambiar el estado. Tabla y panel van dentro de un `SplitPane` para que el usuario pueda ajustar el ancho.

**Justificación:** así no hace falta abrir otra ventana para consultar una incidencia. La descripción no cabe en una columna de la tabla, y en el panel sí. Poner el cambio de estado junto al detalle deja claro *a qué incidencia* se le aplica la acción. El botón "Aplicar cambio" queda deshabilitado si no hay ninguna fila seleccionada, para evitar errores.

## 5. Búsqueda y filtro juntos, encima de la tabla

**Decisión:** el campo de búsqueda (`TextField`), el filtro por estado (`ComboBox`) y el botón "Limpiar filtros" van en una misma barra horizontal, justo encima de la tabla. La búsqueda se aplica mientras se escribe, sin pulsar ningún botón.

**Justificación:** ambos controles modifican lo que se ve en la tabla, así que se colocan juntos y cerca de ella (principio de proximidad). Filtrar al escribir da una respuesta inmediata. El botón "Limpiar filtros" permite volver rápido a ver todo.

## 6. El estado no se transmite solo con color

**Decisión:** cada estado lleva un símbolo además de un color, tanto en la tabla como en los indicadores: ● Abierta, ◐ En proceso, ✔ Resuelta. El texto del estado siempre aparece escrito.

**Justificación:** el enunciado prohíbe usar únicamente el color para transmitir información importante. Una persona daltónica, o que imprima la pantalla en blanco y negro, debe poder distinguir los estados igualmente.

## 7. Alta de incidencias en un diálogo modal con validación

**Decisión:** el botón "+ Nueva incidencia" (arriba a la derecha) abre un `Dialog` con un formulario en `GridPane` (etiqueta a la izquierda, campo a la derecha). Los campos obligatorios llevan asterisco. Si falta el título, aparece un mensaje de error y no se permite guardar.

**Justificación:** crear una incidencia es una acción puntual. Un diálogo la separa de la pantalla principal sin perder el contexto. El `GridPane` alinea etiquetas y campos en columnas, lo que da consistencia. Validar antes de guardar evita incidencias vacías y explica al usuario qué ha hecho mal.

## 8. Barra de estado para el feedback

**Decisión:** en la parte inferior hay una barra que muestra el resultado de la última acción ("✔ Incidencia #2 cambiada a En proceso") y cuántas incidencias se ven con el filtro actual ("Mostrando 5 de 12").

**Justificación:** el enunciado pide feedback cuando el usuario realiza acciones relevantes. Un mensaje fijo abajo informa sin interrumpir como lo haría una ventana emergente. El contador ayuda a entender que hay un filtro activo cuando la tabla muestra menos filas.

## 9. Aspecto visual definido en CSS

**Decisión:** colores, tamaños de letra, márgenes y bordes se definen en un archivo `styles.css` mediante clases (`.kpi-card`, `.kpi-value`, `.estado-abierta`, etc.). El código Java solo asigna las clases.

**Justificación:** separa la presentación de la lógica, como pide el enunciado. Si se quiere cambiar un color, se toca un solo archivo, y todos los elementos del mismo tipo se ven igual, lo que garantiza la consistencia.
