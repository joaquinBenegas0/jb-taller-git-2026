package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;

/**
 * Repositorio de toda la familia Arma (una sola tabla).
 * Es una interfaz: declaramos los mensajes y Spring Data genera la implementación.
 */
public interface ArmaRepository extends JpaRepository<Arma, Long> {

    /** Armas que la tienda vende a un equipo, de la más barata a la más cara. */
    List<Arma> findByEquipoOrderByPrecioAsc(Equipo equipo);

    /** Armas que se pueden comprar con cierto dinero. */
    List<Arma> findByPrecioLessThanEqualOrderByPrecioAsc(int precioMaximo);
}
