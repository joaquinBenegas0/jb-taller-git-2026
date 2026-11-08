package py.edu.uc.lp3.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

/**
 * Carga una partida de ejemplo al arrancar, usando los constructores del dominio
 * (así los datos iniciales también cumplen los invariantes). Como la base es en memoria,
 * en cada arranque los ids son los mismos: armas 1 a 7, jugadores 1 a 4.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final ArmaRepository armas;
    private final JugadorRepository jugadores;

    public DatosIniciales(ArmaRepository armas, JugadorRepository jugadores) {
        this.armas = armas;
        this.jugadores = jugadores;
    }

    @Override
    public void run(String... args) {
        if (armas.count() > 0) {
            return;
        }
        RifleAsalto ak = armas.save(new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS));          // 1
        Francotirador awp = armas.save(new Francotirador("AWP", 4750));                          // 2
        armas.save(new Pistola("Glock-18", 200, Equipo.TERRORISTAS));                            // 3
        armas.save(new Escopeta("Nova", 1050, Equipo.ANTITERRORISTAS, 26, 0.4f, 8, 32, 0.5f, 9, 10f)); // 4
        armas.save(new Subfusil("MP9", 1250, Equipo.ANTITERRORISTAS, 26, 0.65f, 30, 120, 2.1f, 0.7f)); // 5
        Granada he = armas.save(new Granada("HE", 300, Equipo.TERRORISTAS, 98));                  // 6
        Pistola usp = armas.save(new Pistola("USP-S (última bala)", 200, Equipo.ANTITERRORISTAS,
                35, 0.8f, 1, 0, 2.2f));                                                          // 7

        jugadores.save(new Jugador("Sombra", Equipo.TERRORISTAS, 0, 800, ak));                   // 1
        jugadores.save(new Jugador("Halcón", Equipo.ANTITERRORISTAS, 100, 800, awp));            // 2
        jugadores.save(new Jugador("Lince", Equipo.ANTITERRORISTAS, 100, 800, usp));             // 3
        Jugador cuervo = new Jugador("Cuervo", Equipo.TERRORISTAS);                              // 4
        cuervo.equipar(he);
        jugadores.save(cuervo);
    }
}
