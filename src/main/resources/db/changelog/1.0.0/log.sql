--liquibase formatted

--changeset sanvew:280920242138
CREATE TABLE LOG (
    ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    LOG_DATE_TIME TIMESTAMP NOT NULL
);