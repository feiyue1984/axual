DROP TABLE record_error IF EXISTS;

CREATE TABLE record_error  (
    error_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    reference NUMERIC(20),
    description VARCHAR(100)
);
