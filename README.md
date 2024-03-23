# Historial Médico para Mascotas

[![build](https://github.com/ttip-mascotas/back/actions/workflows/build.yaml/badge.svg)](https://github.com/ttip-mascotas/back/actions/workflows/build.yml) [![codecov](https://codecov.io/gh/ttip-mascotas/back/graph/badge.svg?token=XV7QPT3FVO)](https://codecov.io/gh/ttip-mascotas/back)

## Desarollo

### Prerrequisitos

Instalar alguna herramienta para administración de contenedores:

- Docker (Linux/Windows/MacOS)
- Podman + podman-docker + Podman Desktop (opcional)

### Contenedores

```bash
docker-compose up
```

Esto ejecuta tanto PostgreSQL como el cliente pgAdmin.

La conexión a la base de datos se configura en el archivo [`application.yaml`](./src/main/resources/application.yaml):

```yaml
  datasource:
    url: jdbc:postgresql://0.0.0.0:5432/pet_history
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

- `0.0.0.0` apunta al contenedor en el que corre la base de datos
- el usuario y contraseña están definidos en el archivo `compose.yaml`

### Endpoints

La especificación de los endpoints se puede acceder a través de Swagger-UI (OpenAPI v3 Spec), ingresando a <http://127.0.0.1:8080> con la aplicación en ejecución.
