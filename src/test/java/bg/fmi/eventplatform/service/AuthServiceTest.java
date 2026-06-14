package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.LoginRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.AuthResponse;
import bg.fmi.eventplatform.exception.InvalidCredentialsException;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("ivan@gmail.com", "password123", "Ivan", "Ivanov", UserRole.ATTENDEE);
        user = new User(userRequest);
        user.setId(1L);
        user.setPassword("hashed");
    }

    @Test
    void registerCreatesUserAndIssuesToken() {
        when(userService.createUser(userRequest)).thenReturn(user);
        when(jwtService.generateToken("ivan@gmail.com", "ATTENDEE")).thenReturn("token-123");

        AuthResponse response = authService.register(userRequest);

        assertEquals("token-123", response.token());
        assertEquals("ivan@gmail.com", response.user().email());
    }

    @Test
    void loginIssuesTokenForValidCredentials() {
        when(userRepository.findByEmail("ivan@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken("ivan@gmail.com", "ATTENDEE")).thenReturn("token-123");

        AuthResponse response = authService.login(new LoginRequest("ivan@gmail.com", "password123"));

        assertEquals("token-123", response.token());
    }

    @Test
    void loginThrowsWhenUserMissing() {
        when(userRepository.findByEmail("ivan@gmail.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("ivan@gmail.com", "password123")));
    }

    @Test
    void loginThrowsWhenPasswordMismatch() {
        when(userRepository.findByEmail("ivan@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("ivan@gmail.com", "wrong")));
    }

    @Test
    void logoutDelegatesToJwtService() {
        authService.logout("token-123");

        verify(jwtService).invalidate("token-123");
    }
}
