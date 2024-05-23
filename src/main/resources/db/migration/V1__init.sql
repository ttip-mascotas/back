CREATE SEQUENCE IF NOT EXISTS pet_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS medical_visit_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS analysis_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS treatment_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS schedule_per_day_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS dose_control_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS pet
(
    id        BIGINT           NOT NULL,
    name      VARCHAR(128)     NOT NULL,
    photo     VARCHAR(256)     NOT NULL,
    weight    DOUBLE PRECISION NOT NULL,
    birthdate DATE             NOT NULL,
    breed     VARCHAR(128)     NOT NULL,
    fur       VARCHAR(128)     NOT NULL,
    sex       VARCHAR(255)     NOT NULL,
    CONSTRAINT pk_pet PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS medical_visit
(
    pet_id       BIGINT,
    id           BIGINT       NOT NULL,
    address      VARCHAR(256) NOT NULL,
    datetime     TIMESTAMP    NOT NULL,
    specialist   VARCHAR(128) NOT NULL,
    observations VARCHAR(512) NOT NULL,
    CONSTRAINT fk_pet_id FOREIGN KEY (pet_id) REFERENCES pet (id),
    CONSTRAINT pk_medical_visit PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS analysis
(
    pet_id     BIGINT,
    id         BIGINT       NOT NULL,
    name       VARCHAR(256) NOT NULL,
    size       BIGINT       NOT NULL,
    url        VARCHAR(256) NOT NULL,
    text       TEXT,
    created_at TIMESTAMP,
    CONSTRAINT fk_pet_id FOREIGN KEY (pet_id) REFERENCES pet (id),
    CONSTRAINT pk_analysis PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS treatment
(
    pet_id          BIGINT,
    id              BIGINT       NOT NULL,
    medicine        VARCHAR(128) NOT NULL,
    datetime        TIMESTAMP    NOT NULL,
    dose            VARCHAR(128) NOT NULL,
    frequency       INTEGER      NOT NULL,
    number_of_times INTEGER      NOT NULL,
    CONSTRAINT fk_pet_id FOREIGN KEY (pet_id) REFERENCES pet (id),
    CONSTRAINT pk_treatment PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schedule_per_day
(
    treatment_id BIGINT,
    id           BIGINT NOT NULL,
    date         DATE   NOT NULL,
    CONSTRAINT fk_treatment_id FOREIGN KEY (treatment_id) REFERENCES treatment (id),
    CONSTRAINT pk_schedule_per_day PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS dose_control
(
    schedule_per_day_id BIGINT,
    id                  BIGINT                      NOT NULL,
    time                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    supplied            BOOLEAN                     NOT NULL,
    CONSTRAINT fk_schedule_per_day_id FOREIGN KEY (schedule_per_day_id) REFERENCES schedule_per_day (id),
    CONSTRAINT pk_dose_control PRIMARY KEY (id)
);
