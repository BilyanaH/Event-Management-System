package bg.fmi.eventplatform.dto.request;

import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(
        @NotNull Long ticketId
) {}
