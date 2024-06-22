# Historial Médico para Mascotas

[![build](https://github.com/ttip-mascotas/back/actions/workflows/build.yaml/badge.svg)](https://github.com/ttip-mascotas/back/actions/workflows/build.yml) [![codecov](https://codecov.io/gh/ttip-mascotas/back/graph/badge.svg?token=XV7QPT3FVO)](https://codecov.io/gh/ttip-mascotas/back)

## Desarollo

### Prerrequisitos

Instalar alguna herramienta para administración de contenedores:

- Docker (Linux/Windows/MacOS)
- Podman + podman-docker + Podman Desktop (opcional)
- GNU Make

### Contenedores

Para iniciar PostgreSQL y Minio, ejecutar:

```bash
make dev
```

Si además se quiere ejecutar la API dentro de un contenedor:

```bash
make up
```

El comando anterior correrá el build de la imagen necesaria para ejecutar el servicio.

La configuración del entorno de los contenedores está centralizada en el archivo [`.env`](./.env):

```ini
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/pets_history
SPRING_SERVER_PORT=8080

MINIO_USERNAME=miniouser
MINIO_PASSWORD=miniopassword
MINIO_WEBUI_PORT=9001
MINIO_PORT=9000
MINIO_HOST=http://minio:${MINIO_PORT}
MINIO_PUBLIC_BUCKET=public

POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=pets_history
```

- `db` apunta al contenedor en el que corre la base de datos

### Endpoints

La especificación de los endpoints se puede acceder a través de Swagger-UI (OpenAPI v3 Spec), ingresando
a <http://127.0.0.1:8080/swagger-ui/index.html> con la aplicación en ejecución.
