FROM postgres:latest
COPY playground/sql_inserts/comparison_function/*.sql /docker-entrypoint-initdb.d/
