package py.edu.uc.lp3.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Reportable;

/** Junta armas y jugadores en una sola lista de Reportable: dos familias, el mismo protocolo. */
@Service
public class ReporteService {

    private final ArmaService armas;
    private final JugadorService jugadores;

    public ReporteService(ArmaService armas, JugadorService jugadores) {
        this.armas = armas;
        this.jugadores = jugadores;
    }

    @Transactional(readOnly = true)
    public List<Reportable> reportables() {
        List<Reportable> reportables = new ArrayList<>();
        reportables.addAll(armas.listar(null, null));
        reportables.addAll(jugadores.listar());
        return reportables;
    }
}
