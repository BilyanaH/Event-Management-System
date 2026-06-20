--liquibase formatted sql

--changeset fmi:007-create-feedback-table
CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    overall_rating INT NOT NULL,
    comment TEXT,
    venue_rating INT,
    content_rating INT,
    organization_rating INT,
    submitted_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_feedback_event FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE INDEX idx_feedback_user_event ON feedback (user_id, event_id);

--rollback DROP TABLE feedback;