--liquibase formatted sql

--changeset fmi:009-create-event-analytics-table
CREATE TABLE event_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    total_registrations INT NOT NULL DEFAULT 0,
    total_check_ins INT NOT NULL DEFAULT 0,
    total_cancellations INT NOT NULL DEFAULT 0,
    total_feedback INT NOT NULL DEFAULT 0,
    avg_rating DECIMAL(3, 2),
    revenue DECIMAL(12, 2),
    demographics TEXT,
    computed_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_analytics_event FOREIGN KEY (event_id) REFERENCES events(id)
);

--rollback DROP TABLE event_analytics;