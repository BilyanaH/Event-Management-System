--liquibase formatted sql

--changeset fmi:004-create-agenda-items-table
CREATE TABLE agenda_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    speaker_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    location_room VARCHAR(100),
    order_index INT NOT NULL,
    type VARCHAR(32),
    CONSTRAINT fk_agenda_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_agenda_speaker FOREIGN KEY (speaker_id) REFERENCES speakers(id)
);

CREATE INDEX idx_agenda_event_order ON agenda_items (event_id, order_index);

--rollback DROP TABLE agenda_items;