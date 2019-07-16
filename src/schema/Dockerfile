FROM postgres:12-alpine

ENV POSTGRES_USER reidit_api
ENV POSTGRES_PASSWORD youshouldusearealpassword
ENV POSTGRES_DB reidit

COPY 1-schema.sql /docker-entrypoint-initdb.d/
