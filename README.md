# CaloIslands 0.1.0

Primera base funcional del plugin independiente de aldeas, bosses y eventos PvE.

## Incluye
- Core y scheduler central.
- `/caloislands` + `/ci`.
- Estado/reload.
- Aldea Goblin start/stop/status.
- Duración 40 min y auto-start configurable.
- Centro/radio configurable.
- Participantes en memoria por aparición.
- Piel y Colmillo con PDC.
- Caps 20 Pieles / 3 Colmillos.
- Drops configurables por categoría Goblin.

## Tags de mobs
- `caloislands_goblin_normal`
- `caloislands_goblin_special`
- `caloislands_goblin_elite`
- `caloislands_goblin_captain`
- `caloislands_goblin_king`

## Compilar
`mvn clean package`

## Primera prueba
`/ci status`
`/ci goblin setcenter`
`/ci goblin start`
`/ci goblin give piel 5`
`/ci goblin give colmillo 1`
`/ci goblin stop`
