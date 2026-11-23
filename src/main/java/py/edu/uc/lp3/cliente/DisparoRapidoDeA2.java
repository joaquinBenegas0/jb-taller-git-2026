package py.edu.uc.lp3.cliente;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Cliente HTTP de A2 (Matías). Arma el cuerpo con los nombres de campo de su OpenAPI:
 * POST {ruta-disparo-rapido} con estabilidad y municionActual.
 * Si A2 cambia la ruta alcanza con companeros.a2.ruta-disparo-rapido; si cambia un campo, se toca solo esta clase.
 */
@Component
public class DisparoRapidoDeA2 implements ReglaRemotaDeDisparoRapido {

    private final ClienteDeCompanero http;
    private final String ruta;

    public DisparoRapidoDeA2(@Value("${companeros.a2.nombre}") String nombre,
                             @Value("${companeros.a2.url}") String url,
                             @Value("${companeros.a2.ruta-disparo-rapido}") String ruta,
                             RestClient.Builder builder) {
        this.http = new ClienteDeCompanero("A2", nombre, url, builder);
        this.ruta = ruta;
    }

    @Override
    public JsonNode validar(float estabilidad, int municionActual) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estabilidad", estabilidad);
        cuerpo.put("municionActual", municionActual);
        return http.post(ruta, cuerpo);
    }

    @Override
    public String companero() {
        return http.nombre();
    }
}
