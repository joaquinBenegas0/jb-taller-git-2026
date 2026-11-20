package py.edu.uc.lp3.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/** Lógica de negocio y transacciones de los jugadores: alta con sus topes y búsqueda. */
@Service
public class JugadorService {

    private final JugadorRepository jugadores;
    private final ArmaService armas;

    public JugadorService(JugadorRepository jugadores, ArmaService armas) {
        this.jugadores = jugadores;
        this.armas = armas;
    }

    @Transactional(readOnly = true)
    public List<Jugador> listar() {
        return jugadores.findAll();
    }

    @Transactional(readOnly = true)
    public Jugador buscar(Long id) {
        if (id == null) {
            throw new ValorIlegalException("Falta el id del jugador");
        }
        return jugadores.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("jugador", id));
    }

    /** blindaje, dinero y armaId son opcionales: sin blindaje, $800 y sin arma. */
    @Transactional
    public Jugador crear(String nombre, Equipo equipo, Integer blindaje, Integer dinero, Long armaId) {
        int blindajeInicial = blindaje == null ? 0 : blindaje;
        int dineroInicial = dinero == null ? Jugador.DINERO_INICIAL : dinero;
        validar(nombre, equipo, blindajeInicial, dineroInicial);
        Arma arma = armaId == null ? null : armas.buscar(armaId);
        return jugadores.save(new Jugador(nombre, equipo, blindajeInicial, dineroInicial, arma));
    }

    private static void validar(String nombre, Equipo equipo, int blindaje, int dinero) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValorIlegalException("El jugador necesita un nombre");
        }
        if (equipo == null) {
            throw new ValorIlegalException("El jugador debe pertenecer a un equipo");
        }
        if (blindaje < 0 || blindaje > Jugador.BLINDAJE_MAXIMO) {
            throw new ValorIlegalException("El blindaje va de 0 a " + Jugador.BLINDAJE_MAXIMO);
        }
        if (dinero < 0 || dinero > Jugador.DINERO_MAXIMO) {
            throw new ValorIlegalException("El dinero va de 0 a " + Jugador.DINERO_MAXIMO);
        }
    }
}
