# jb-taller-git-2026
Servicio REST en Spring Boot (Java 21) con el modelado de armas de **Counter-Strike 2**.
Taller de Git y POO — Lenguaje de Programación 3 (CYT646).

## Cómo ejecutarlo

```
./mvnw spring-boot:run
```

- `GET /` → estado del servicio
- `GET /api/armas/francotirador?nombre=AWP&precio=4750` → construye un francotirador con parámetros de la URL
- `GET /api/armas/inventario` → cada arma del inventario se usa a través del tipo padre `Arma`

## Diagrama de clases

```mermaid
classDiagram
    class Arma {
        <<abstract>>
        -String nombre
        -int precio
        -Equipo equipo
        +usar() String*
        +mostrarEnTienda() String
        +obtenerPrecio() int
        +obtenerEquipo() Equipo
    }
    class ArmaDeFuego {
        <<abstract>>
        -int danio
        -float precision
        -int capacidadCargador
        -int municionCargador
        -int municionReserva
        -float tiempoRecarga
        +usar() String
        +disparar() String
        +recargar() String
        #balasPorDisparo() int
        #calcularDanioPorBala() int
    }
    class Granada {
        -int danio
        -float radioExplosion
        -float tiempoActivacion
        -boolean esLetal
        -String efecto
        -boolean lanzada
        -boolean detonada
        +usar() String
        +lanzar() String
        +detonar() String
    }
    class Pistola {
        -boolean modoRafaga
        +activarModoRafaga() void
        +desactivarModoRafaga() void
        #balasPorDisparo() int
    }
    class RifleAsalto {
        -ModoDisparo modoDisparo
        -float cadenciaDisparo
        +cambiarModo(ModoDisparo m) void
        #balasPorDisparo() int
    }
    class Francotirador {
        -float zoom
        -float penetracion
        -boolean zoomActivo
        +activarZoom() void
        +desactivarZoom() void
        #calcularDanioPorBala() int
    }
    class Escopeta {
        -int numeroPerdigones
        -float distanciaEfectiva
        #calcularDanioPorBala() int
    }
    class Subfusil {
        -float controlRetroceso
    }
    class Equipo {
        <<enumeration>>
        TERRORISTAS
        ANTITERRORISTAS
    }
    class ModoDisparo {
        <<enumeration>>
        SEMI
        RAFAGA
        AUTOMATICO
        +balasPorDisparo() int
    }

    Arma <|-- ArmaDeFuego
    Arma <|-- Granada
    ArmaDeFuego <|-- Pistola
    ArmaDeFuego <|-- RifleAsalto
    ArmaDeFuego <|-- Francotirador
    ArmaDeFuego <|-- Escopeta
    ArmaDeFuego <|-- Subfusil
    Arma --> Equipo
    RifleAsalto --> ModoDisparo
```
