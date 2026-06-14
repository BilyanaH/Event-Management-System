package bg.fmi.eventplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName
) {}
