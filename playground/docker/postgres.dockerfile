FROM postgres:latest
COPY postgres/*.sql /docker-entrypoint-initdb.d/
