FROM mysql:8.0.29

ENV MYSQL_DATABASE=swtp
ENV MYSQL_ALLOW_EMPTY_PASSWORD=true

COPY ./conf/* /etc/mysql/conf.d
COPY ./initdb/* /docker-entrypoint-initdb.d/  