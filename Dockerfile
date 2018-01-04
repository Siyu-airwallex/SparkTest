FROM postgres:10.1

ENV PGDATA /var/lib/postgresql/data/pgdata

ADD ["data_dec", "/var/lib/postgresql/data/pgdata"]









