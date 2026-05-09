package bg.fmi.eventplatform.exception;

public class UserNotFoundException extends RuntimeException {
    public static final String baseMessage = "User not found";

    public UserNotFoundException() {
        super(baseMessage);
    }

    public UserNotFoundException(String email) {
        super(baseMessage + ": email:" + email);
    }

    public UserNotFoundException(Long userId) {
        super(baseMessage + ": user id:" + userId);
    }
}
