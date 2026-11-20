package py.edu.uc.lp3.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Escopeta;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3.domain.Subfusil;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.JugadorService;

/**
 * Carga una partida de ejemplo al arrancar. Usa los constructores del dominio y guarda a través
 * de los services, así los datos iniciales pasan por las mismas validaciones que un POST.
 * Como la base es en memoria, en cada arranque los ids son los mismos: armas 1 a 7, jugadores 1 a 4.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final ArmaService armas;
    private final JugadorService jugadores;

    public DatosIniciales(ArmaService armas, JugadorService jugadores) {
        this.armas = armas;
        this.jugadores = jugadores;
    }

    @Override
    public void run(String... args) {
        if (armas.hayArmas()) {
            return;
        }
        RifleAsalto ak = armas.crear(new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS));          // 1
        Francotirador awp = armas.crear(new Francotirador("AWP", 4750));                          // 2
        armas.crear(new Pistola("Glock-18", 200, Equipo.TERRORISTAS));                            // 3
        armas.crear(new Escopeta("Nova", 1050, Equipo.ANTITERRORISTAS, 26, 0.4f, 8, 32, 0.5f, 9, 10f)); // 4
        armas.crear(new Subfusil("MP9", 1250, Equipo.ANTITERRORISTAS, 26, 0.65f, 30, 120, 2.1f, 0.7f)); // 5
        Granada he = armas.crear(new Granada("HE", 300, Equipo.TERRORISTAS, 98));                  // 6
        Pistola usp = armas.crear(new Pistola("USP-S (última bala)", 200, Equipo.ANTITERRORISTAS,
                35, 0.8f, 1, 0, 2.2f));                                                           // 7

        jugadores.crear("Sombra", Equipo.TERRORISTAS, 0, 800, ak.obtenerId());                    // 1
        jugadores.crear("Halcón", Equipo.ANTITERRORISTAS, 100, 800, awp.obtenerId());             // 2
        jugadores.crear("Lince", Equipo.ANTITERRORISTAS, 100, 800, usp.obtenerId());              // 3
        jugadores.crear("Cuervo", Equipo.TERRORISTAS, null, null, he.obtenerId());                // 4
    }
}
