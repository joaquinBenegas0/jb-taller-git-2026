package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.domain.Equipo;

/** Cuerpo de POST /api/reglas/danio: quién dispara a quién, cómo está el objetivo y cuánto daño sale del arma. */
@Schema(description = "Tirador, objetivo y daño del disparo")
public record ConsultaDeDanio(
        @Schema(description = "Nombre del tirador (solo para el mensaje)", example = "Sombra") String tirador,
        @Schema(description = "Equipo del tirador", example = "TERRORISTAS") Equipo equipoTirador,
        @Schema(description = "Nombre del objetivo (solo para el mensaje)", example = "Halcón") String objetivo,
        @Schema(description = "Equipo del objetivo", example = "ANTITERRORISTAS") Equipo equipoObjetivo,
        @Schema(description = "Vida del objetivo antes del impacto (0 a 100)", example = "100") Integer vidaObjetivo,
        @Schema(description = "Blindaje del objetivo antes del impacto (0 a 100)", example = "100") Integer blindajeObjetivo,
        @Schema(description = "Daño total que sale del arma", example = "180") Integer danio) {
}
