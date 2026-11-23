package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Cuerpo de POST /api/escenarios/disparos-rapidos. */
@Schema(description = "Disparo rápido con francotirador")
public record DisparoRapido(
        @Schema(description = "Jugador que dispara (tiene que tener un francotirador)", example = "2") Long tiradorId,
        @Schema(description = "Jugador al que le dispara", example = "1") Long objetivoId,
        @Schema(description = "Distancia en metros", example = "40") float distancia,
        @Schema(description = "true: aguanta la respiración (estabilidad 1.0); false u omitido: respira (0.5)",
                example = "true") Boolean aguantaRespiracion) {

    public boolean aguanta() {
        return Boolean.TRUE.equals(aguantaRespiracion);
    }
}
