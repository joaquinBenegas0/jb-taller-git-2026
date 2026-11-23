package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.domain.JugadaCruzada;

/** Jugadas del escenario cruzado que aceptaron A2 y A3. */
public interface JugadaCruzadaRepository extends JpaRepository<JugadaCruzada, Long> {
}
