package py.edu.uc.lp3.rest.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Escopeta;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3.domain.Subfusil;

/**
 * JSON de creación de un arma. Jackson lee la propiedad "tipo" y elige el record;
 * cada record sabe qué constructor del dominio llamar. El id no viaja: lo asigna la base.
 * Si faltan los valores opcionales se usa el constructor simple (valores estándar de CS2).
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NuevaArma.NuevaPistola.class, name = "pistola"),
        @JsonSubTypes.Type(value = NuevaArma.NuevoRifle.class, name = "rifle"),
        @JsonSubTypes.Type(value = NuevaArma.NuevoFrancotirador.class, name = "francotirador"),
        @JsonSubTypes.Type(value = NuevaArma.NuevaEscopeta.class, name = "escopeta"),
        @JsonSubTypes.Type(value = NuevaArma.NuevoSubfusil.class, name = "subfusil"),
        @JsonSubTypes.Type(value = NuevaArma.NuevaGranada.class, name = "granada")
})
public sealed interface NuevaArma {

    /** Construye el arma del dominio; los invariantes los valida ArmaService antes de guardar. */
    Arma crear();

    record NuevaPistola(String nombre, int precio, Equipo equipo, Integer danio, Float precision,
                        Integer capacidadCargador, Integer municionReserva, Float tiempoRecarga)
            implements NuevaArma {
        @Override
        public Arma crear() {
            if (danio == null) {
                return new Pistola(nombre, precio, equipo);
            }
            return new Pistola(nombre, precio, equipo, danio, requerido(precision, "precision"),
                    requerido(capacidadCargador, "capacidadCargador"),
                    requerido(municionReserva, "municionReserva"), requerido(tiempoRecarga, "tiempoRecarga"));
        }
    }

    record NuevoRifle(String nombre, int precio, Equipo equipo, Integer danio, Float precision,
                      Integer capacidadCargador, Integer municionReserva, Float tiempoRecarga,
                      Float cadenciaDisparo) implements NuevaArma {
        @Override
        public Arma crear() {
            if (danio == null) {
                return new RifleAsalto(nombre, precio, equipo);
            }
            return new RifleAsalto(nombre, precio, equipo, danio, requerido(precision, "precision"),
                    requerido(capacidadCargador, "capacidadCargador"),
                    requerido(municionReserva, "municionReserva"), requerido(tiempoRecarga, "tiempoRecarga"),
                    requerido(cadenciaDisparo, "cadenciaDisparo"));
        }
    }

    record NuevoFrancotirador(String nombre, int precio, Equipo equipo, Integer danio, Float precision,
                              Integer capacidadCargador, Integer municionReserva, Float tiempoRecarga,
                              Float zoom, Float penetracion) implements NuevaArma {
        @Override
        public Arma crear() {
            if (equipo == null) {
                return new Francotirador(nombre, precio);
            }
            if (danio == null) {
                return new Francotirador(nombre, precio, equipo);
            }
            return new Francotirador(nombre, precio, equipo, danio, requerido(precision, "precision"),
                    requerido(capacidadCargador, "capacidadCargador"),
                    requerido(municionReserva, "municionReserva"), requerido(tiempoRecarga, "tiempoRecarga"),
                    requerido(zoom, "zoom"), requerido(penetracion, "penetracion"));
        }
    }

    record NuevaEscopeta(String nombre, int precio, Equipo equipo, int danio, float precision,
                         int capacidadCargador, int municionReserva, float tiempoRecarga,
                         int numeroPerdigones, float distanciaEfectiva) implements NuevaArma {
        @Override
        public Arma crear() {
            return new Escopeta(nombre, precio, equipo, danio, precision, capacidadCargador,
                    municionReserva, tiempoRecarga, numeroPerdigones, distanciaEfectiva);
        }
    }

    record NuevoSubfusil(String nombre, int precio, Equipo equipo, int danio, float precision,
                         int capacidadCargador, int municionReserva, float tiempoRecarga,
                         float controlRetroceso) implements NuevaArma {
        @Override
        public Arma crear() {
            return new Subfusil(nombre, precio, equipo, danio, precision, capacidadCargador,
                    municionReserva, tiempoRecarga, controlRetroceso);
        }
    }

    /** Con "efecto" es táctica (no letal); con "danio" es explosiva. */
    record NuevaGranada(String nombre, int precio, Equipo equipo, Integer danio, String efecto)
            implements NuevaArma {
        @Override
        public Arma crear() {
            if (efecto != null) {
                return new Granada(nombre, precio, equipo, efecto);
            }
            return new Granada(nombre, precio, equipo, requerido(danio, "danio o efecto"));
        }
    }

    private static <T> T requerido(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("Falta el campo " + campo);
        }
        return valor;
    }
}
