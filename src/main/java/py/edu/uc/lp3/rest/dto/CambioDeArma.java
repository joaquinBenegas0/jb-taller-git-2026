package py.edu.uc.lp3.rest.dto;

/**
 * Cuerpo del PATCH: solo los campos que la tienda puede cambiar.
 * Un campo omitido (null) queda como estaba.
 */
public record CambioDeArma(String nombre, Integer precio) {
}
