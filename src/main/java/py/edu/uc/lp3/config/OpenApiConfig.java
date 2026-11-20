package py.edu.uc.lp3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Datos generales del documento OpenAPI 3 (springdoc lo genera a partir de los controllers).
 * Sin "servers" fijos: springdoc usa la URL desde la que se pidió el documento,
 * así funciona igual en localhost o desde la notebook de un compañero.
 * JSON: /v3/api-docs · YAML: /v3/api-docs.yaml · interfaz: /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI documentoOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("jb-taller-git-2026 · CS2")
                        .version("POO-09")
                        .description("""
                                Servicio de Joaquín Benegas (LP3, CYT646) sobre Counter-Strike 2.

                                **Regla 1 · munición** (`POST /api/reglas/municion`): un disparo gasta las balas de su \
                                modo (SEMI 1, RAFAGA 3, AUTOMATICO 5) y con el cargador vacío no se dispara → 409 \
                                `MUNICION_AGOTADA`.

                                **Regla 2 · daño con blindaje** (`POST /api/reglas/danio`): solo se le dispara a un \
                                enemigo vivo (si no → 422 `DISPARO_NO_PERMITIDO`); el blindaje absorbe hasta la mitad \
                                del daño y el resto baja la vida.

                                Las dos reglas no tocan la base: sirven para que otro servicio valide una jugada. \
                                `POST /api/simulaciones/disparos` aplica las dos reglas a jugadores guardados y persiste \
                                el resultado. Todo error responde `{status, codigo, excepcion, mensaje}`.""")
                        .contact(new Contact().name("Joaquín Benegas").url("https://github.com/joaquinBenegas0"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
