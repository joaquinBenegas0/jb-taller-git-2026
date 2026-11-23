package py.edu.uc.lp3.cliente;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;

import py.edu.uc.lp3exceptions.CompaneroNoDisponibleException;
import py.edu.uc.lp3exceptions.RechazoDeCompaneroException;

/**
 * Conexión HTTP (RestClient) hacia el servicio de un compañero (A2 o A3).
 * Solo conoce la URL base (de application.properties o variable de entorno); la ruta y el cuerpo
 * los arma MunicionDeA2 o DanioDeA3 según el OpenAPI de cada uno. No comparte clases con su proyecto.
 * Un 4xx es un rechazo de su regla; un 5xx o la falta de conexión es que no está disponible.
 */
public class ClienteDeCompanero {

    private final String rol;
    private final String nombre;
    private final String urlBase;
    private final RestClient http;

    public ClienteDeCompanero(String rol, String nombre, String urlBase, RestClient.Builder builder) {
        this.rol = rol;
        this.nombre = nombre;
        this.urlBase = urlBase;
        // Si un compañero está apagado no queremos que el escenario quede colgado.
        SimpleClientHttpRequestFactory tiempos = new SimpleClientHttpRequestFactory();
        tiempos.setConnectTimeout(Duration.ofSeconds(3));
        tiempos.setReadTimeout(Duration.ofSeconds(5));
        this.http = builder.clone().baseUrl(urlBase).requestFactory(tiempos).build();
    }

    /** POST con un cuerpo JSON a una ruta del compañero; devuelve su respuesta si la regla acepta. */
    public JsonNode post(String ruta, Object cuerpo) {
        try {
            return http.post()
                    .uri(ruta)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (HttpClientErrorException e) {
            throw new RechazoDeCompaneroException(rol, nombre, e.getStatusCode().value(),
                    e.getResponseBodyAsString());
        } catch (RestClientResponseException e) {
            throw new CompaneroNoDisponibleException(rol, nombre, urlBase + ruta,
                    "HTTP " + e.getStatusCode().value());
        } catch (ResourceAccessException e) {
            throw new CompaneroNoDisponibleException(rol, nombre, urlBase + ruta, e.getMessage());
        }
    }

    public String rol() {
        return rol;
    }

    public String nombre() {
        return nombre;
    }

    public String urlBase() {
        return urlBase;
    }
}
