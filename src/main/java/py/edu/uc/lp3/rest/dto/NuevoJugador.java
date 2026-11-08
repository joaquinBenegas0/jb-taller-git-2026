package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.Equipo;

/** Cuerpo del POST de jugadores. blindaje, dinero y armaId son opcionales. */
public record NuevoJugador(String nombre, Equipo equipo, Integer blindaje, Integer dinero, Long armaId) {
}
