package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.domain.Impacto;

public interface ImpactoRepository extends JpaRepository<Impacto, Long> {

    /** Historial de disparos de un tirador, del más viejo al más nuevo. */
    List<Impacto> findByTiradorIdOrderByIdAsc(Long tiradorId);
}
