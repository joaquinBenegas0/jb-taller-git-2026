package py.edu.uc.lp3.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.ArmaDeFuego;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Escopeta;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3.domain.Subfusil;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3exceptions.MunicionAgotadaException;
import py.edu.uc.lp3exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/**
 * Lógica de negocio y transacciones de la familia Arma: CRUD, disparo, recarga y granadas.
 * Es la única clase que usa ArmaRepository (además de SimulacionService y ReporteService para leer).
 */
@Service
public class ArmaService {

    /** Resultado de probar un francotirador recién construido (endpoint de POO-06). */
    public record PruebaDeFrancotirador(Francotirador arma, String disparo, String disparoConDistancia) {
    }

    private final ArmaRepository armas;
    private final JugadorRepository jugadores;
    private final ValidadorDeArmas validador;
    private final ReglaDeMunicion reglaDeMunicion;

    public ArmaService(ArmaRepository armas, JugadorRepository jugadores, ValidadorDeArmas validador,
                       ReglaDeMunicion reglaDeMunicion) {
        this.armas = armas;
        this.jugadores = jugadores;
        this.validador = validador;
        this.reglaDeMunicion = reglaDeMunicion;
    }

    // ---- CRUD ----

    @Transactional(readOnly = true)
    public List<Arma> listar(Equipo equipo, Integer precioMaximo) {
        if (equipo != null) {
            return armas.findByEquipoOrderByPrecioAsc(equipo);
        }
        if (precioMaximo != null) {
            return armas.findByPrecioLessThanEqualOrderByPrecioAsc(precioMaximo);
        }
        return armas.findAll();
    }

    @Transactional(readOnly = true)
    public Arma buscar(Long id) {
        if (id == null) {
            throw new ValorIlegalException("Falta el id del arma");
        }
        return armas.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("arma", id));
    }

    @Transactional(readOnly = true)
    public boolean hayArmas() {
        return armas.count() > 0;
    }

    /** Valida los invariantes y recién entonces guarda: si algo falla no se guarda nada (400). */
    @Transactional
    public <T extends Arma> T crear(T arma) {
        validador.validar(arma);
        return armas.save(arma);
    }

    /** PATCH: cambia solo los campos enviados, con las mismas reglas que al crear. */
    @Transactional
    public Arma actualizar(Long id, String nombre, Integer precio) {
        Arma arma = buscar(id);
        if (nombre != null) {
            validador.validarNombre(nombre);
        }
        if (precio != null) {
            validador.validarPrecio(precio);
        }
        if (nombre != null) {
            arma.renombrar(nombre);
        }
        if (precio != null) {
            arma.cambiarPrecio(precio);
        }
        return armas.save(arma);
    }

    /** La tienda retira el arma: quien la tenía equipada la suelta y la fila desaparece. */
    @Transactional
    public void retirar(Long id) {
        Arma arma = buscar(id);
        for (Jugador jugador : jugadores.findByArma(arma)) {
            jugador.soltarArma();
            jugadores.save(jugador);
        }
        armas.delete(arma);
    }

    // ---- Disparo (regla 1) y recarga ----

    /** Dispara sin indicar distancia. Con el cargador vacío no lanza error: hace "click". */
    public String disparar(ArmaDeFuego arma) {
        return efectuarDisparo(arma, arma.calcularDanioPorBala(), "");
    }

    /** Sobrecarga: la misma acción a una distancia; más allá del alcance efectivo el daño cae. */
    public String disparar(ArmaDeFuego arma, float distanciaMetros) {
        validarDistancia(distanciaMetros);
        String detalle = " a " + distanciaMetros + " m";
        if (distanciaMetros > arma.alcanceEfectivo()) {
            detalle += " (fuera del alcance efectivo de " + arma.alcanceEfectivo() + " m)";
        }
        return efectuarDisparo(arma, danioPorBalaA(arma, distanciaMetros), detalle);
    }

    /** Daño de cada bala a esa distancia: más allá del alcance efectivo cae en proporción. */
    public int danioPorBalaA(ArmaDeFuego arma, float distanciaMetros) {
        validarDistancia(distanciaMetros);
        float alcance = arma.alcanceEfectivo();
        int danioPorBala = arma.calcularDanioPorBala();
        if (distanciaMetros > alcance) {
            danioPorBala = Math.round(danioPorBala * alcance / distanciaMetros);
        }
        return danioPorBala;
    }

    /** Mueve balas de la reserva al cargador sin pasarse de la capacidad. */
    public String recargar(ArmaDeFuego arma) {
        int faltan = arma.obtenerCapacidadCargador() - arma.obtenerMunicionCargador();
        if (faltan == 0) {
            return arma.obtenerNombre() + ": el cargador ya está lleno";
        }
        if (arma.obtenerMunicionReserva() == 0) {
            return arma.obtenerNombre() + ": no queda munición de reserva";
        }
        int cargadas = Math.min(faltan, arma.obtenerMunicionReserva());
        arma.actualizarMunicion(arma.obtenerMunicionCargador() + cargadas, arma.obtenerMunicionReserva() - cargadas);
        return arma.obtenerNombre() + " recarga " + cargadas + " bala(s) en " + arma.obtenerTiempoRecarga()
                + " s [" + arma.obtenerMunicionCargador() + "/" + arma.obtenerMunicionReserva() + "]";
    }

    // ---- Granadas ----

    public String lanzar(Granada granada) {
        if (granada.fueLanzada()) {
            return granada.obtenerNombre() + ": ya fue lanzada";
        }
        granada.marcarLanzada(System.currentTimeMillis());
        return granada.obtenerNombre() + " lanzada, detona en " + granada.obtenerTiempoActivacion() + " s";
    }

    public String detonar(Granada granada) {
        if (!granada.fueLanzada()) {
            return granada.obtenerNombre() + ": no se puede detonar sin lanzarla";
        }
        if (granada.detono()) {
            return granada.obtenerNombre() + ": ya detonó";
        }
        long transcurrido = System.currentTimeMillis() - granada.obtenerMomentoLanzamiento();
        if (transcurrido < granada.obtenerTiempoActivacion() * 1000) {
            return granada.obtenerNombre() + ": todavía no se activó";
        }
        granada.marcarDetonada();
        return granada.usar();
    }

    // ---- Endpoints de POO-04 y POO-06 (sin persistencia) ----

    /** Inventario de ejemplo: mezcla constructores simples, sobrecargados y completos, todos validados. */
    public List<Arma> inventarioDeEjemplo() {
        List<Arma> inventario = List.of(
                new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS),
                new Francotirador("AWP", 4750),
                new Pistola("Glock-18", 200, Equipo.TERRORISTAS),
                new Escopeta("Nova", 1050, Equipo.ANTITERRORISTAS, 26, 0.4f, 8, 32, 0.5f, 9, 10f),
                new Subfusil("MP9", 1250, Equipo.ANTITERRORISTAS, 26, 0.65f, 30, 120, 2.1f, 0.7f),
                new Granada("HE", 300, Equipo.TERRORISTAS, 98),
                new Granada("Flashbang", 200, Equipo.ANTITERRORISTAS, "ceguera temporal"));
        inventario.forEach(validador::validar);
        return inventario;
    }

    /** Valida un francotirador recién construido y prueba las dos versiones de disparar. */
    public PruebaDeFrancotirador probar(Francotirador arma, float distancia) {
        validador.validar(arma);
        String disparo = disparar(arma);
        String disparoConDistancia = disparar(arma, distancia);
        return new PruebaDeFrancotirador(arma, disparo, disparoConDistancia);
    }

    /** Único lugar del disparo "suelto" que descuenta munición: usa la regla 1. */
    private String efectuarDisparo(ArmaDeFuego arma, int danioPorBala, String detalle) {
        ReglaDeMunicion.Resultado municion;
        try {
            municion = reglaDeMunicion.aplicar(arma.obtenerNombre(), arma.obtenerMunicionCargador(),
                    arma.obtenerMunicionReserva(), arma.balasPorDisparo());
        } catch (MunicionAgotadaException e) {
            return arma.obtenerNombre() + ": click... cargador vacío, hay que recargar";
        }
        arma.actualizarMunicion(municion.municionRestante(), arma.obtenerMunicionReserva());
        int danioTotal = danioPorBala * municion.balasGastadas();
        return arma.obtenerNombre() + " dispara " + municion.balasGastadas() + " bala(s)" + detalle + ": "
                + danioTotal + " de daño [" + arma.obtenerMunicionCargador() + "/" + arma.obtenerMunicionReserva() + "]";
    }

    private static void validarDistancia(float distanciaMetros) {
        if (distanciaMetros < 0f) {
            throw new ValorIlegalException("La distancia no puede ser negativa");
        }
    }
}
