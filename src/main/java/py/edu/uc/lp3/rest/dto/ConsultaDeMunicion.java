package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.domain.ModoDisparo;

/** Cuerpo de POST /api/reglas/municion: el estado del arma justo antes de disparar. */
@Schema(description = "Estado del arma antes del disparo")
public record ConsultaDeMunicion(
        @Schema(description = "Nombre del arma (solo para el mensaje)", example = "AK-47") String arma,
        @Schema(description = "Balas en el cargador antes de disparar", example = "30", minimum = "0") Integer municionCargador,
        @Schema(description = "Balas de reserva (opcional, 0 si se omite)", example = "90", minimum = "0") Integer municionReserva,
        @Schema(description = "Modo de disparo: SEMI gasta 1 bala, RAFAGA 3, AUTOMATICO 5 (SEMI si se omite)",
                example = "AUTOMATICO") ModoDisparo modo) {
}
