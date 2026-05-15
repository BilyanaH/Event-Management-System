package bg.fmi.eventplatform.dto.request;

import bg.fmi.eventplatform.vo.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Request for creating a new user account, recieved from the controller.
 * throws MethodArgumentNotValidException if fields are not valid
 **/

public record UserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull UserRole role
) {}
