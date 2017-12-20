FROM postgres

ENV PGDATA /var/lib/postgresql/data/pgdata

ADD ["full_data", "/var/lib/postgresql/data/pgdata"]









