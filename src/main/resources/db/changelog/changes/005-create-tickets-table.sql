--liquibase formatted sql

--changeset fmi:005-create-tickets-table
CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    quantity_available INT NOT NULL,
    quantity_sold INT NOT NULL DEFAULT 0,
    sale_start TIMESTAMP,
    sale_end TIMESTAMP,
    status VARCHAR(32) NOT NULL,
    CONSTRAINT fk_ticket_event FOREIGN KEY (event_id) REFERENCES events(id)
);

--rollback DROP TABLE tickets;