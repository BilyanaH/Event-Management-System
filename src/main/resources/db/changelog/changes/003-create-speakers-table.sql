--liquibase formatted sql

--changeset fmi:003-create-speakers-table
CREATE TABLE speakers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    name VARCHAR(200) NOT NULL,
    bio TEXT,
    company VARCHAR(200),
    title_position VARCHAR(200),
    photo_url VARCHAR(500),
    website_url VARCHAR(500),
    CONSTRAINT fk_speaker_user FOREIGN KEY (user_id) REFERENCES users(id)
);

--rollback DROP TABLE speakers;