--liquibase formatted sql

--changeset fmi:006-create-registrations-table
CREATE TABLE registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    ticket_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    confirmation_code VARCHAR(20) NOT NULL UNIQUE,
    registered_at TIMESTAMP NOT NULL,
    checked_in_at TIMESTAMP,
    CONSTRAINT fk_registration_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_registration_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_registration_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);

CREATE INDEX idx_registration_user_event ON registrations (user_id, event_id);

--rollback DROP TABLE registrations;