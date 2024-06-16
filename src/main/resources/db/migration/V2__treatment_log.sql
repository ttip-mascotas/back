CREATE TABLE IF NOT EXISTS treatment_log
(
    treatment_id BIGINT,
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    datetime     TIMESTAMP WITH TIME ZONE                NOT NULL,
    administered BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_treatment_log PRIMARY KEY (id),
    CONSTRAINT fk_treatment_id FOREIGN KEY (treatment_id) REFERENCES treatment (id)
);

ALTER TABLE IF EXISTS dose_control
    DROP CONSTRAINT IF EXISTS fk_schedule_per_day_id;

ALTER TABLE IF EXISTS schedule_per_day
    DROP CONSTRAINT IF EXISTS fk_treatment_id;

DROP TABLE IF EXISTS dose_control CASCADE;

DROP TABLE IF EXISTS schedule_per_day CASCADE;
