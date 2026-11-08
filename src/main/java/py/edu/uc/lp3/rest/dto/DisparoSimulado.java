package py.edu.uc.lp3.rest.dto;

/** Cuerpo del POST de simulación: quién dispara, a quién y a qué distancia (en metros). */
public record DisparoSimulado(Long tiradorId, Long objetivoId, float distancia) {
}
