package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.rest.dto.CambioDeArma;
import py.edu.uc.lp3.rest.dto.NuevaArma;
import py.edu.uc.lp3exceptions.RecursoNoEncontradoException;

/**
 * CRUD REST de la familia Arma. Traduce HTTP: las reglas las validan las clases del dominio
 * y la persistencia la hace el repositorio (una interfaz).
 */
@RestController
@RequestMapping(ApiPaths.ARMAS)
public class ArmaController {

    private final ArmaRepository armas;
    private final JugadorRepository jugadores;

    public ArmaController(ArmaRepository armas, JugadorRepository jugadores) {
        this.armas = armas;
        this.jugadores = jugadores;
    }

    /** Lista lo persistido; con ?equipo= o ?precioMaximo= usa las consultas del repositorio. */
    @GetMapping
    public List<Arma> listar(@RequestParam(value = "equipo", required = false) Equipo equipo,
                             @RequestParam(value = "precioMaximo", required = false) Integer precioMaximo) {
        if (equipo != null) {
            return armas.findByEquipoOrderByPrecioAsc(equipo);
        }
        if (precioMaximo != null) {
            return armas.findByPrecioLessThanEqualOrderByPrecioAsc(precioMaximo);
        }
        return armas.findAll();
    }

    @GetMapping(ApiPaths.POR_ID)
    public Arma buscar(@PathVariable("id") Long id) {
        return buscarOFallar(id);
    }

    /** Crea con el constructor del dominio: si rompe un invariante no se guarda nada (400). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Arma crear(@RequestBody NuevaArma pedido) {
        return armas.save(pedido.crear());
    }

    /** PATCH: cambia solo los campos enviados, a través de mensajes del dominio que validan. */
    @PatchMapping(ApiPaths.POR_ID)
    @Transactional
    public Arma actualizar(@PathVariable("id") Long id, @RequestBody CambioDeArma cambio) {
        Arma arma = buscarOFallar(id);
        if (cambio.nombre() != null) {
            arma.renombrar(cambio.nombre());
        }
        if (cambio.precio() != null) {
            arma.cambiarPrecio(cambio.precio());
        }
        return armas.save(arma);
    }

    /** Borrar = la tienda retira el arma: quien la tenía equipada la suelta y la fila desaparece. */
    @DeleteMapping(ApiPaths.POR_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void borrar(@PathVariable("id") Long id) {
        Arma arma = buscarOFallar(id);
        for (Jugador jugador : jugadores.findByArma(arma)) {
            jugador.soltarArma();
            jugadores.save(jugador);
        }
        armas.delete(arma);
    }

    private Arma buscarOFallar(Long id) {
        return armas.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("arma", id));
    }
}
