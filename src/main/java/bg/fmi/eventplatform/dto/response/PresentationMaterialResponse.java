package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.PresentationMaterial;

import java.time.LocalDateTime;

public record PresentationMaterialResponse(
        Long id,
        Long speakerId,
        Long agendaItemId,
        String fileName,
        String fileUrl,
        String fileType,
        LocalDateTime uploadedAt
) {
    public static PresentationMaterialResponse fromEntity(PresentationMaterial material) {
        return new PresentationMaterialResponse(
                material.getId(),
                material.getSpeaker().getId(),
                material.getAgendaItem() == null ? null : material.getAgendaItem().getId(),
                material.getFileName(),
                material.getFileUrl(),
                material.getFileType(),
                material.getUploadedAt()
        );
    }
}
