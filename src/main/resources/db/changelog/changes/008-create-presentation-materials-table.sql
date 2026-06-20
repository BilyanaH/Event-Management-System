--liquibase formatted sql

--changeset fmi:008-create-presentation-materials-table
CREATE TABLE presentation_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    speaker_id BIGINT NOT NULL,
    agenda_item_id BIGINT,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    uploaded_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_material_speaker FOREIGN KEY (speaker_id) REFERENCES speakers(id),
    CONSTRAINT fk_material_agenda_item FOREIGN KEY (agenda_item_id) REFERENCES agenda_items(id)
);

--rollback DROP TABLE presentation_materials;