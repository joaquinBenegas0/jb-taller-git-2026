package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Jugador;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    /** Jugadores que tienen equipada un arma (para que la suelten antes de borrarla). */
    List<Jugador> findByArma(Arma arma);
}
