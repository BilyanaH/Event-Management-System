package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.vo.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        UserRole role,
        LocalDateTime createdAt
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}