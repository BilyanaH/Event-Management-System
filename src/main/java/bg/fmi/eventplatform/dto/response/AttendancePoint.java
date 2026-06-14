package bg.fmi.eventplatform.dto.response;

import java.time.LocalDate;

public record AttendancePoint(
        LocalDate day,
        long count
) {}
