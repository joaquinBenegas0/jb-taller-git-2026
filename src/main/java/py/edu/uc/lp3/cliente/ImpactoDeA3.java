package py.edu.uc.lp3.cliente;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Cliente HTTP de A3 (Marcelo). Arma el cuerpo con los nombres de campo de su OpenAPI:
 * POST {ruta-impacto} con modelo (nombre del arma en su catálogo de CS2), distancia, vida y blindaje.
 * Si A3 cambia la ruta alcanza con companeros.a3.ruta-impacto; si cambia un campo, se toca solo esta clase.
 */
@Component
public class ImpactoDeA3 implements ReglaRemotaDeImpacto {

    private final ClienteDeCompanero http;
    private final String ruta;

    public ImpactoDeA3(@Value("${companeros.a3.nombre}") String nombre,
                       @Value("${companeros.a3.url}") String url,
                       @Value("${companeros.a3.ruta-impacto}") String ruta,
                       RestClient.Builder builder) {
        this.http = new ClienteDeCompanero("A3", nombre, url, builder);
        this.ruta = ruta;
    }

    @Override
    public JsonNode validar(String modelo, float distancia, int vida, int blindaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("modelo", modelo);
        cuerpo.put("distancia", distancia);
        cuerpo.put("vida", vida);
        cuerpo.put("blindaje", blindaje);
        return http.post(ruta, cuerpo);
    }

    @Override
    public String companero() {
        return http.nombre();
    }
}
