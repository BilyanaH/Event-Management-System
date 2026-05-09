package bg.fmi.eventplatform.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public static String message = "Email already in use";

    public EmailAlreadyUsedException(String email) {
        super(message + " : " + email);
    }
}
