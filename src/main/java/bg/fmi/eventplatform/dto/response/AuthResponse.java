package bg.fmi.eventplatform.dto.response;

public record AuthResponse(
        String token,
        UserResponse user
) {}
