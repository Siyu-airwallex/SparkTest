FROM postgres

# Environment variables
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD postgres
ENV POSTGRES_DB validation

# Create Database
RUN	mkdir /usr/sql
RUN	chmod a+rx /usr/sql

ADD ["sql/initial.sql", "/docker-entrypoint-initdb.d/initial.sql"]
#ADD ["sql/createTable.sql", "/docker-entrypoint-initdb.d/createTable.sql"]
#ADD ["sql/loadData.sql", "/docker-entrypoint-initdb.d/loadData.sql"]

#ADD ["BANKDIRECTORYPLUS_V3_FULL_20171027.csv", "/usr/sql/data/swift.csv"]

RUN ["apt-get", "update"]
RUN ["apt-get", "install", "-y", "vim"]

RUN /docker-entrypoint.sh postgres & sleep 30 && killall postgres

#RUN rm /docker-entrypoint-initdb.d/initial.sql









