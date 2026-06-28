package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Feedback;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.FeedbackRequest;
import bg.fmi.eventplatform.dto.response.FeedbackResponse;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private GrokService grokService;

    @InjectMocks
    private FeedbackService feedbackService;

    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(5L);
        event = new Event();
        event.setId(10L);
    }

    @Test
    void submitSavesWhenCheckedIn() {
        FeedbackRequest req = new FeedbackRequest(5, "great", 4, 5, 5);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(feedbackRepository.existsByUserIdAndEventId(5L, 10L)).thenReturn(false);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(eq(5L), eq(10L), eq(RegistrationStatus.CHECKED_IN))).thenReturn(true);
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(inv -> {
            Feedback f = inv.getArgument(0);
            f.setId(1L);
            return f;
        });

        FeedbackResponse response = feedbackService.submit(10L, req, user);

        assertEquals(5, response.overallRating());
    }

    @Test
    void submitThrowsWhenNotCheckedIn() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(feedbackRepository.existsByUserIdAndEventId(5L, 10L)).thenReturn(false);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(eq(5L), eq(10L), eq(RegistrationStatus.CHECKED_IN))).thenReturn(false);

        assertThrows(ValidationException.class,
                () -> feedbackService.submit(10L, new FeedbackRequest(5, null, null, null, null), user));
    }

    @Test
    void submitRejectsDuplicate() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(feedbackRepository.existsByUserIdAndEventId(5L, 10L)).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> feedbackService.submit(10L, new FeedbackRequest(5, null, null, null, null), user));
    }
}
