package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.exception.EmailAlreadyUsedException;
import bg.fmi.eventplatform.exception.UserNotFoundException;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.service.UserService;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
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

        userRequest = new UserRequest(
                "ivan@gmail.com",
                "password123",
                "Ivan",
                "Ivanov",
                UserRole.ATTENDEE
        );
    }

    @Test
    void createUserSavesToDatabase() {
        User result = userService.createUser(userRequest);

        assertNotNull(result.getId());
        assertEquals("ivan@gmail.com", result.getEmail());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Ivanov", result.getLastName());
        assertEquals(UserRole.ATTENDEE, result.getRole());
    }

    @Test
    void createUserThrowsWhenEmailExists() {
        userService.createUser(userRequest);

        assertThrows(EmailAlreadyUsedException.class, () -> userService.createUser(userRequest));
    }

    @Test
    void getUserByIdReturnsUser() {
        User saved = userService.createUser(userRequest);

        User result = userService.getUserById(saved.getId());

        assertEquals(saved.getId(), result.getId());
        assertEquals("ivan@gmail.com", result.getEmail());
    }

    @Test
    void getUserByIdThrowsWhenNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getUserByEmailReturnsUser() {
        userService.createUser(userRequest);

        User result = userService.getUserByEmail("ivan@gmail.com");

        assertEquals("ivan@gmail.com", result.getEmail());
    }

    @Test
    void getUserByEmailThrowsWhenNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("unknown@gmail.com"));
    }

    @Test
    void getAllUsersReturnsList() {
        userService.createUser(userRequest);
        userService.createUser(new UserRequest(
                "petar@gmail.com",
                "password456",
                "Petar",
                "Petrov",
                UserRole.ORGANIZER
        ));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void updateUserUpdatesFields() {
        User saved = userService.createUser(userRequest);

        User updatedUser = new User();
        updatedUser.setFirstName("Petar");
        updatedUser.setLastName("Petrov");
        updatedUser.setEmail("petar@gmail.com");

        User result = userService.updateUser(saved.getId(), updatedUser);

        assertEquals("Petar", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("petar@gmail.com", result.getEmail());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void deleteUserRemovesFromDatabase() {
        User saved = userService.createUser(userRequest);

        userService.deleteUser(saved.getId());

        assertFalse(userRepository.existsById(saved.getId()));
    }

    @Test
    void deleteUserThrowsWhenNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
    }
}