package py.edu.uc.lp3.cliente;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Lo que el escenario necesita de A2 (Matías): que su regla de disparo rápido acepte el disparo,
 * es decir, que el francotirador esté estabilizado y tenga al menos dos balas.
 * Devuelve la respuesta de A2 si acepta; si rechaza lanza RechazoDeCompaneroException.
 */
public interface ReglaRemotaDeDisparoRapido {

    JsonNode validar(float estabilidad, int municionActual);

    String companero();
}
