package bg.fmi.eventplatform.dto.request;

import bg.fmi.eventplatform.domain.Speaker;
import bg.fmi.eventplatform.domain.User;
import jakarta.validation.constraints.NotBlank;

public record SpeakerRequest(
        Long userId,
        @NotBlank String name,
        String bio,
        String company,
        String titlePosition,
        String photoUrl,
        String websiteUrl
) {
    public static Speaker toEntity(SpeakerRequest request, User user) {
        Speaker speaker = new Speaker();
        speaker.setUser(user);
        speaker.setName(request.name());
        speaker.setBio(request.bio());
        speaker.setCompany(request.company());
        speaker.setTitlePosition(request.titlePosition());
        speaker.setPhotoUrl(request.photoUrl());
        speaker.setWebsiteUrl(request.websiteUrl());
        return speaker;
    }
}
