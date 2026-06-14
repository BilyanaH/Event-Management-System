package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.EventAnalytics;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.response.AttendancePoint;
import bg.fmi.eventplatform.dto.response.EventAnalyticsResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {

    private final EventRepository eventRepository;
    private final EventAnalyticsRepository analyticsRepository;
    private final RegistrationRepository registrationRepository;
    private final FeedbackRepository feedbackRepository;
    private final TicketRepository ticketRepository;

    public AnalyticsService(EventRepository eventRepository,
                            EventAnalyticsRepository analyticsRepository,
                            RegistrationRepository registrationRepository,
                            FeedbackRepository feedbackRepository,
                            TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.analyticsRepository = analyticsRepository;
        this.registrationRepository = registrationRepository;
        this.feedbackRepository = feedbackRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public EventAnalyticsResponse dashboard(Long eventId, User principal) throws AccessDeniedException {
        Event event = loadEventForOrganizer(eventId, principal);

        long totalRegistrations = registrationRepository.countByEventId(eventId);
        long totalCheckIns = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CHECKED_IN);
        long totalCancellations = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CANCELLED);
        long totalFeedback = feedbackRepository.countByEventId(eventId);
        Double avg = feedbackRepository.findAverageOverallRating(eventId);
        BigDecimal revenue = ticketRepository.sumRevenueByEventId(eventId);

        EventAnalytics analytics = analyticsRepository.findByEventId(eventId).orElseGet(EventAnalytics::new);
        analytics.setEvent(event);
        analytics.setTotalRegistrations((int) totalRegistrations);
        analytics.setTotalCheckIns((int) totalCheckIns);
        analytics.setTotalCancellations((int) totalCancellations);
        analytics.setTotalFeedback((int) totalFeedback);
        analytics.setAvgRating(avg == null ? null : BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        analytics.setRevenue(revenue == null ? BigDecimal.ZERO : revenue);
        analytics.setComputedAt(LocalDateTime.now());

        return EventAnalyticsResponse.fromEntity(analyticsRepository.save(analytics));
    }

    public List<AttendancePoint> attendance(Long eventId, User principal) throws AccessDeniedException {
        loadEventForOrganizer(eventId, principal);
        return registrationRepository.countRegistrationsPerDay(eventId).stream()
                .map(row -> new AttendancePoint(toLocalDate(row[0]), ((Number) row[1]).longValue()))
                .toList();
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate ld) return ld;
        if (value instanceof Date d) return d.toLocalDate();
        if (value instanceof java.util.Date d) return new Date(d.getTime()).toLocalDate();
        return LocalDate.parse(value.toString());
    }

    private Event loadEventForOrganizer(Long eventId, User principal) throws AccessDeniedException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + eventId));
        if (!event.getOrganizer().getId().equals(principal.getId())) {
            throw new AccessDeniedException("User is not the organizer of this event");
        }
        return event;
    }
}
