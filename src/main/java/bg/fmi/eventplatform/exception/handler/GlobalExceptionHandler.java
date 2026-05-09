package bg.fmi.eventplatform.exception.handler;

import bg.fmi.eventplatform.dto.ErrorResponse;
import bg.fmi.eventplatform.exception.EmailAlreadyUsedException;
import bg.fmi.eventplatform.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final int BAD_REQUEST_CODE = 400;
    private static final int NOT_FOUND_CODE = 404;
    private static final int CONFLICT_CODE = 409;

    /**
     * Handles validation errors for method arguments with Spring validation annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBindValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return new ErrorResponse(LocalDateTime.now(), BAD_REQUEST_CODE, message, request.getRequestURI());
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyUsed(EmailAlreadyUsedException ex, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), CONFLICT_CODE, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), NOT_FOUND_CODE, ex.getMessage(), request.getRequestURI());
    }
}
