CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL,
    email    VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    role VARCHAR(10) NOT NULL CHECK ( role IN ('USER', 'ADMIN'))
);