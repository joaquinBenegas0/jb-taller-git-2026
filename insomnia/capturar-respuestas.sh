#!/usr/bin/env bash
# Recorre el escenario de POO-09 como la colección de Insomnia y guarda cada respuesta
# en insomnia/respuestas/, para corregir aunque A2 o A3 no estén prendidos.
# Uso (con A1 recién arrancado y A2 y A3 prendidos):
#   A2_URL=http://IP_MATIAS:8080 A3_URL=http://IP_MARCELO:8080 insomnia/capturar-respuestas.sh
set -u
A1=${A1_URL:-http://localhost:8080}
A2=${A2_URL:-http://localhost:8082}
A3=${A3_URL:-http://localhost:8083}
DIR="$(dirname "$0")/respuestas"
mkdir -p "$DIR"

pedir() {
  local archivo=$1 metodo=$2 url=$3 cuerpo=${4:-}
  local args=(-s -X "$metodo" -w '\n%{http_code}' "$url")
  if [ -n "$cuerpo" ]; then
    args+=(-H 'Content-Type: application/json' -d "$cuerpo")
  fi
  local salida codigo
  salida=$(curl "${args[@]}")
  codigo=${salida##*$'\n'}
  salida=${salida%$'\n'*}
  printf '{"pedido":"%s %s","status":%s,"respuesta":%s}\n' "$metodo" "$url" "$codigo" "${salida:-null}" > "$DIR/$archivo.json"
  echo "$codigo  $archivo"
}

disparo() { printf '{"tiradorId":%s,"objetivoId":%s,"distancia":40,"aguantaRespiracion":%s}' "$1" "$2" "$3"; }
ESC="$A1/api/escenarios/disparos-rapidos"

pedir 00-openapi-a1 GET "$A1/v3/api-docs"
pedir 00-openapi-a2-matias GET "$A2/v3/api-docs"
pedir 00-openapi-a3-marcelo GET "$A3/v3/api-docs"
pedir 01-sombra-antes GET "$A1/api/jugadores/1"
pedir 02-feliz-halcon-a-sombra POST "$ESC" "$(disparo 2 1 true)"
pedir 03-jugadas-despues-del-feliz GET "$ESC"
pedir 04-sombra-eliminado GET "$A1/api/jugadores/1"
pedir 05-awp-con-3-balas GET "$A1/api/armas/2"
pedir 06-rechazo-a3-sombra-eliminado POST "$ESC" "$(disparo 2 1 true)"
pedir 07-awp-sigue-con-3 GET "$A1/api/armas/2"
pedir 08-cuervo-antes GET "$A1/api/jugadores/4"
pedir 09-rechazo-a2-sin-aguantar POST "$ESC" "$(disparo 2 4 false)"
pedir 10-cuervo-sigue-igual GET "$A1/api/jugadores/4"
pedir 11-halcon-a-cuervo-aguantando POST "$ESC" "$(disparo 2 4 true)"
pedir 12-rechazo-a2-una-bala POST "$ESC" "$(disparo 2 4 true)"
pedir 13-jugadas-finales GET "$ESC"
