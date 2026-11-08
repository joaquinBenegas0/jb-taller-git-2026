package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * Raíz de la jerarquía: todo lo que se puede comprar en la tienda.
 * Toda la familia se guarda en una sola tabla (ARMA); la columna TIPO dice qué hija es cada fila.
 * En el JSON el subtipo viaja en la propiedad "tipo".
 * No hay setters: JPA lee y escribe los campos directamente (acceso por campo)
 * y el resto del sistema solo cambia el estado con mensajes que validan.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pistola.class, name = "pistola"),
        @JsonSubTypes.Type(value = RifleAsalto.class, name = "rifle"),
        @JsonSubTypes.Type(value = Francotirador.class, name = "francotirador"),
        @JsonSubTypes.Type(value = Escopeta.class, name = "escopeta"),
        @JsonSubTypes.Type(value = Subfusil.class, name = "subfusil"),
        @JsonSubTypes.Type(value = Granada.class, name = "granada")
})
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
public abstract class Arma implements Reportable {

    /** Recompensa estándar de CS2 por eliminación; las hijas que pagan distinto la redefinen. */
    protected static final int RECOMPENSA_ESTANDAR = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int precio;

    @Enumerated(EnumType.STRING)
    private Equipo equipo;

    /** Solo para JPA: protected, así ninguna clase de afuera arma un Arma vacía. */
    protected Arma() {
    }

    protected Arma(String nombre, int precio, Equipo equipo) {
        validarNombre(nombre);
        validarPrecio(precio);
        if (equipo == null) {
            throw new IllegalArgumentException("El arma debe pertenecer a un equipo");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.equipo = equipo;
    }

    /**
     * Mensaje común a todas las armas: "usate".
     * El padre no puede implementarlo porque cada tipo se usa distinto:
     * un arma de fuego dispara (y controla su munición), una granada se lanza (y controla su activación).
     */
    public abstract String usar();

    /** Cómo se muestra cualquier arma en la tienda: igual para todas. */
    public String mostrarEnTienda() {
        return nombre + " - $" + precio + " (" + equipo + ")";
    }

    /** La tienda puede renombrar un arma, con la misma regla que al construirla. */
    public void renombrar(String nuevoNombre) {
        validarNombre(nuevoNombre);
        this.nombre = nuevoNombre;
    }

    /** La tienda puede cambiar el precio, con la misma regla que al construirla. */
    public void cambiarPrecio(int nuevoPrecio) {
        validarPrecio(nuevoPrecio);
        this.precio = nuevoPrecio;
    }

    /** Dinero que cobra el tirador cuando elimina a alguien con esta arma. */
    public int recompensaPorEliminacion() {
        return RECOMPENSA_ESTANDAR;
    }

    @Override
    public String identificacion() {
        return "Arma #" + id + " " + nombre;
    }

    @Override
    public String reporteDeCombate() {
        return mostrarEnTienda();
    }

    public Long obtenerId() {
        return id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerPrecio() {
        return precio;
    }

    public Equipo obtenerEquipo() {
        return equipo;
    }

    @Override
    public String toString() {
        return mostrarEnTienda();
    }

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El arma necesita un nombre");
        }
    }

    private static void validarPrecio(int precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
    }
}
