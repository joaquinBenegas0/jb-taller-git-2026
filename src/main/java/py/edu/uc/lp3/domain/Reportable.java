package py.edu.uc.lp3.domain;

/**
 * Protocolo del dominio: cualquier cosa del juego que puede informar
 * cómo está en la partida. No importa si es un arma o un jugador:
 * quien habla con un Reportable no pregunta de qué familia es.
 */
public interface Reportable {

    /** Quién es, en una frase corta (por ejemplo "Arma #1 AK-47"). */
    String identificacion();

    /** Estado de combate actual, en lenguaje del juego. */
    String reporteDeCombate();
}
