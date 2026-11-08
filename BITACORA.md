# Bitácora de asistencia de IA

| Dato | Valor |
|---|---|
| Asistente | Claude (Anthropic), en claude.ai |
| Modelo LLM | Claude Opus 5.5 (`claude-opus-5-5`) |
| Ejercicios | POO-04 (Taller de Git), POO-06 (paquetes, constructores y sobrecarga) y POO-08 (CRUD REST, H2, reglas y excepciones) |

## Resumen de prompts

1. Le pasé la consigna del taller y le pedí que me guíe paso a paso desde el repositorio que ya tenía creado.
2. Le mostré mi diagrama de CS2 de POO-03 y elegimos la opción B; le pedí pasar las clases a Java con campos privados y validaciones en los constructores.
3. Le pedí ayuda con errores reales: Spring Boot 4.1.1 en vez de 3.x, el puerto 8080 ocupado y el `git push` que fallaba por credenciales.
4. Le pedí los dos controllers (`GET /` y construcción por URL) y después el método abstracto `usar()` con un inventario que no use `if` por tipo.
5. Le pedí el diagrama Mermaid para el README y los pasos para configurar SSH en Windows.
6. Para POO-06 le pasé el enunciado, la rúbrica y el template de paquetes, y le pedí reorganizar los paquetes, agregar constructores sobrecargados y sobrecargar `disparar` con una distancia.

### POO-08

7. Le pasé la consigna de POO-08 de Classroom (enunciado, rúbrica y template de paquetes) y le pedí que revise el estado de mi repositorio y arme la solución sobre mi modelado de CS2.
8. Decidimos persistir la familia `Arma` en una sola tabla (`SINGLE_TABLE`), sumar `Jugador` e `Impacto`, y usar como reglas la munición (cada disparo gasta balas; con el cargador vacío no se dispara) y el daño con blindaje y recompensa por eliminación.
9. Le pedí la jerarquía de excepciones (`Cs2Exception` con tres hijas) con un handler por tipo, la interfaz `Reportable` para armas y jugadores, el CRUD con PATCH y DELETE, y una partida inicial con ids fijos para poder probar.
10. Le pedí la colección de Insomnia con todos los pedidos que exige el enunciado, pruebas unitarias de las dos reglas, el diagrama Mermaid y las respuestas de «Qué hay que pensar» para el README.
11. El asistente compiló el código y corrió las reglas del dominio en su entorno, pero no pudo descargar las dependencias de Spring; el arranque con `./mvnw spring-boot:run` y el recorrido de Insomnia los verifiqué yo en mi VM.

## Cómo lo usé

El asistente propuso el diseño y el código; yo ejecuté cada paso en mi entorno (VM Lubuntu y Git Bash en Windows), revisé las clases antes de cada commit, compilé con `./mvnw -q compile`, probé los endpoints en el navegador y resolví los errores que aparecieron. Los commits y el push los hice yo.
