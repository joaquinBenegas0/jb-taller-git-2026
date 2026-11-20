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
 * Es una entidad: solo guarda estado. Las validaciones y las reglas del juego viven en la capa
 * service (ValidadorDeArmas, ArmaService, ReglaDeMunicion, ReglaDeDanio); acá no se lanza ninguna excepción.
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
        this.nombre = nombre;
        this.precio = precio;
        this.equipo = equipo;
    }

    /**
     * Mensaje común a todas las armas: cómo se usa. El padre no puede responderlo porque cada tipo
     * se usa distinto: un arma de fuego se dispara, una granada se lanza. Solo describe, no cambia estado.
     */
    public abstract String usar();

    /** Cómo se muestra cualquier arma en la tienda: igual para todas. */
    public String mostrarEnTienda() {
        return nombre + " - $" + precio + " (" + equipo + ")";
    }

    /** Lo llama ArmaService después de validar el nombre. */
    public void renombrar(String nuevoNombre) {
        this.nombre = nuevoNombre;
    }

    /** Lo llama ArmaService después de validar el precio. */
    public void cambiarPrecio(int nuevoPrecio) {
        this.precio = nuevoPrecio;
    }

    /** Dato del tipo de arma: lo que cobra el tirador cuando elimina a alguien con ella. */
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
}
