package bg.fmi.eventplatform.dto;

import java.time.LocalDateTime;

/**
    * A record representing an error response to be sent to the client.
 *
 * @param timestamp
 * @param status
 * @param message
 * @param path
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String message,
        String path
) {}
