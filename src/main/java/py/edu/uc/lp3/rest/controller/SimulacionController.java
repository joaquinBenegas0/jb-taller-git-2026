package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.rest.dto.DisparoSimulado;
import py.edu.uc.lp3.service.SimulacionService;

/**
 * Recurso de la simulación: /api/simulaciones/disparos.
 * Solo traduce HTTP: SimulacionService busca a los jugadores, aplica las dos reglas y guarda
 * el resultado en una transacción. Si una regla falla, la base queda igual.
 */
@RestController
@RequestMapping(ApiPaths.DISPAROS)
public class SimulacionController {

    private final SimulacionService simulaciones;

    public SimulacionController(SimulacionService simulaciones) {
        this.simulaciones = simulaciones;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Impacto simular(@RequestBody DisparoSimulado disparo) {
        return simulaciones.simular(disparo.tiradorId(), disparo.objetivoId(), disparo.distancia());
    }

    /** Historial persistido; con ?tiradorId= filtra por tirador. */
    @GetMapping
    public List<Impacto> historial(@RequestParam(value = "tiradorId", required = false) Long tiradorId) {
        return simulaciones.historial(tiradorId);
    }
}
