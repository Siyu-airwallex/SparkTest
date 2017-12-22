FROM postgres:10.1

ENV PGDATA /var/lib/postgresql/data/pgdata

ADD ["data", "/var/lib/postgresql/data/pgdata"]









