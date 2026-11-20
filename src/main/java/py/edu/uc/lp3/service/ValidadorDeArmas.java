package py.edu.uc.lp3.service;

import org.springframework.stereotype.Component;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.ArmaDeFuego;
import py.edu.uc.lp3.domain.Escopeta;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3.domain.Subfusil;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/**
 * Invariantes de la familia Arma. Antes vivían en los constructores; ahora la entidad solo
 * guarda estado y ArmaService llama a este validador antes de guardar o usar un arma.
 */
@Component
public class ValidadorDeArmas {

    public void validar(Arma arma) {
        if (arma == null) {
            throw new ValorIlegalException("Falta el arma");
        }
        validarNombre(arma.obtenerNombre());
        validarPrecio(arma.obtenerPrecio());
        if (arma.obtenerEquipo() == null) {
            throw new ValorIlegalException("El arma debe pertenecer a un equipo");
        }
        if (arma instanceof ArmaDeFuego deFuego) {
            validarArmaDeFuego(deFuego);
        }
        if (arma instanceof RifleAsalto rifle && rifle.obtenerCadencia() <= 0f) {
            throw new ValorIlegalException("La cadencia debe ser mayor a 0");
        }
        if (arma instanceof Francotirador francotirador) {
            validarFrancotirador(francotirador);
        }
        if (arma instanceof Escopeta escopeta) {
            validarEscopeta(escopeta);
        }
        if (arma instanceof Subfusil subfusil
                && (subfusil.obtenerControlRetroceso() < 0f || subfusil.obtenerControlRetroceso() > 1f)) {
            throw new ValorIlegalException("El control de retroceso va de 0 a 1");
        }
        if (arma instanceof Granada granada) {
            validarGranada(granada);
        }
    }

    public void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValorIlegalException("El arma necesita un nombre");
        }
    }

    public void validarPrecio(int precio) {
        if (precio <= 0) {
            throw new ValorIlegalException("El precio debe ser mayor a 0");
        }
    }

    private void validarArmaDeFuego(ArmaDeFuego arma) {
        if (arma.obtenerDanio() <= 0) {
            throw new ValorIlegalException("El daño debe ser mayor a 0");
        }
        if (arma.obtenerPrecision() < 0f || arma.obtenerPrecision() > 1f) {
            throw new ValorIlegalException("La precisión va de 0 a 1");
        }
        if (arma.obtenerCapacidadCargador() <= 0) {
            throw new ValorIlegalException("El cargador debe tener capacidad");
        }
        if (arma.obtenerMunicionCargador() < 0 || arma.obtenerMunicionCargador() > arma.obtenerCapacidadCargador()) {
            throw new ValorIlegalException("La munición del cargador va de 0 a " + arma.obtenerCapacidadCargador());
        }
        if (arma.obtenerMunicionReserva() < 0) {
            throw new ValorIlegalException("La reserva no puede ser negativa");
        }
        if (arma.obtenerTiempoRecarga() <= 0f) {
            throw new ValorIlegalException("El tiempo de recarga debe ser mayor a 0");
        }
    }

    private void validarFrancotirador(Francotirador arma) {
        if (arma.obtenerZoom() < 1f) {
            throw new ValorIlegalException("El zoom debe ser al menos 1x");
        }
        if (arma.obtenerPenetracion() < 0f || arma.obtenerPenetracion() > 1f) {
            throw new ValorIlegalException("La penetración va de 0 a 1");
        }
    }

    private void validarEscopeta(Escopeta arma) {
        if (arma.obtenerNumeroPerdigones() <= 0) {
            throw new ValorIlegalException("Debe disparar al menos un perdigón");
        }
        if (arma.obtenerDistanciaEfectiva() <= 0f) {
            throw new ValorIlegalException("La distancia efectiva debe ser mayor a 0");
        }
    }

    private void validarGranada(Granada granada) {
        if (granada.esLetal() && granada.obtenerDanio() <= 0) {
            throw new ValorIlegalException("Una granada letal debe hacer daño");
        }
        if (!granada.esLetal() && granada.obtenerDanio() != 0) {
            throw new ValorIlegalException("Una granada no letal no hace daño");
        }
        if (granada.obtenerRadioExplosion() <= 0f) {
            throw new ValorIlegalException("El radio de explosión debe ser mayor a 0");
        }
        if (granada.obtenerTiempoActivacion() < 0f) {
            throw new ValorIlegalException("El tiempo de activación no puede ser negativo");
        }
        if (granada.obtenerEfecto() == null || granada.obtenerEfecto().isBlank()) {
            throw new ValorIlegalException("La granada debe tener un efecto");
        }
    }
}
