\c validation;
COPY public.bankdirectory FROM '/usr/sql/data/swift.csv' WITH ( FORMAT csv, DELIMITER '	', HEADER true, NULL 'NULL');