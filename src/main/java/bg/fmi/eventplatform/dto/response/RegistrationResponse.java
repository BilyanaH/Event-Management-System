package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.Registration;
import bg.fmi.eventplatform.vo.RegistrationStatus;

import java.time.LocalDateTime;

public record RegistrationResponse(
        Long id,
        Long userId,
        Long eventId,
        Long ticketId,
        RegistrationStatus status,
        String confirmationCode,
        LocalDateTime registeredAt,
        LocalDateTime checkedInAt
) {
    public static RegistrationResponse fromEntity(Registration registration) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getUser().getId(),
                registration.getEvent().getId(),
                registration.getTicket().getId(),
                registration.getStatus(),
                registration.getConfirmationCode(),
                registration.getRegisteredAt(),
                registration.getCheckedInAt()
        );
    }
}
