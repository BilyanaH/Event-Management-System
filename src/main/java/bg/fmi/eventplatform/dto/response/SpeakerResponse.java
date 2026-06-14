package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.Speaker;

public record SpeakerResponse(
        Long id,
        Long userId,
        String name,
        String bio,
        String company,
        String titlePosition,
        String photoUrl,
        String websiteUrl
) {
    public static SpeakerResponse fromEntity(Speaker speaker) {
        return new SpeakerResponse(
                speaker.getId(),
                speaker.getUser() == null ? null : speaker.getUser().getId(),
                speaker.getName(),
                speaker.getBio(),
                speaker.getCompany(),
                speaker.getTitlePosition(),
                speaker.getPhotoUrl(),
                speaker.getWebsiteUrl()
        );
    }
}
