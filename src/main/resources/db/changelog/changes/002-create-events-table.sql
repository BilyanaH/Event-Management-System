--liquibase formatted sql

--changeset fmi:002-create-events-table
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organizer_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    venue VARCHAR(255),
    city VARCHAR(255),
    venue_address VARCHAR(255),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    capacity INT,
    status VARCHAR(32) NOT NULL,
    category VARCHAR(64),
    image_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_event_organizer FOREIGN KEY (organizer_id) REFERENCES users(id)
);

--rollback DROP TABLE events;