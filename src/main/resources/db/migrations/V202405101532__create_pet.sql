CREATE TABLE IF NOT EXISTS public.pet
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(128)     NOT NULL,
    photo     VARCHAR(256)     NOT NULL,
    weight    DOUBLE PRECISION NOT NULL,
    birthdate TIMESTAMP        NOT NULL,
    breed     VARCHAR(128)     NOT NULL,
    fur       VARCHAR(128)     NOT NULL,
    sex       VARCHAR(128)     NOT NULL
)
