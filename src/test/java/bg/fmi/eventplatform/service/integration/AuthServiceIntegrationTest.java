package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.dto.request.LoginRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.AuthResponse;
import bg.fmi.eventplatform.exception.InvalidCredentialsException;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.service.AuthService;
import bg.fmi.eventplatform.service.JwtService;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AgendaItemRepository agendaItemRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private EventAnalyticsRepository analyticsRepository;

    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        analyticsRepository.deleteAll();
        feedbackRepository.deleteAll();
        registrationRepository.deleteAll();
        agendaItemRepository.deleteAll();
        ticketRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
        userRequest = new UserRequest("ivan@gmail.com", "password123", "Ivan", "Ivanov", UserRole.ATTENDEE);
    }

    @Test
    void registerIssuesValidToken() {
        AuthResponse response = authService.register(userRequest);

        assertNotNull(response.token());
        assertTrue(jwtService.isValid(response.token()));
        assertEquals("ivan@gmail.com", jwtService.extractEmail(response.token()));
    }

    @Test
    void loginIssuesTokenForValidCredentials() {
        authService.register(userRequest);

        AuthResponse response = authService.login(new LoginRequest("ivan@gmail.com", "password123"));

        assertNotNull(response.token());
        assertEquals("ivan@gmail.com", response.user().email());
    }

    @Test
    void loginThrowsForBadPassword() {
        authService.register(userRequest);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("ivan@gmail.com", "wrong")));
    }

    @Test
    void logoutInvalidatesToken() {
        AuthResponse response = authService.register(userRequest);

        authService.logout(response.token());

        assertFalse(jwtService.isValid(response.token()));
    }
}
