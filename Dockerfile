FROM postgres:10.0

ENV PGDATA /var/lib/postgresql/data/pgdata

ADD ["full_data", "/var/lib/postgresql/data/pgdata"]









