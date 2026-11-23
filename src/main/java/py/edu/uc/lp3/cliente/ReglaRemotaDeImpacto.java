package py.edu.uc.lp3.cliente;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Lo que el escenario necesita de A3 (Marcelo): que su regla de vida acepte el impacto sobre el objetivo,
 * es decir, que el objetivo no esté eliminado y el arma y los valores existan en su juego.
 * Devuelve la respuesta de A3 si acepta; si rechaza lanza RechazoDeCompaneroException.
 */
public interface ReglaRemotaDeImpacto {

    JsonNode validar(String modelo, float distancia, int vida, int blindaje);

    String companero();
}
