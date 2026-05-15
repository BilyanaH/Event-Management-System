package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.exception.EmailAlreadyUsedException;
import bg.fmi.eventplatform.exception.UserNotFoundException;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(
                "ivan@gmail.com",
                "password123",
                "Ivan",
                "Ivanov",
                UserRole.ATTENDEE
        );

        user = new User(userRequest);
        user.setId(1L);
    }

    @Test
    void createUserReturnSavedUser() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(userRequest);

        assertEquals("ivan@gmail.com", result.getEmail());
        assertEquals("Ivan", result.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUseThrowWhenEmailExists() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> userService.createUser(userRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByIdReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("ivan@gmail.com", result.getEmail());
    }

    @Test
    void getUserByIdThrowWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getUserByEmailReturnUser() {
        when(userRepository.findByEmail("ivan@gmail.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("ivan@gmail.com");

        assertEquals("ivan@gmail.com", result.getEmail());
    }

    @Test
    void getUserByEmailThrowWhenNotFound() {
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("unknown@gmail.com"));
    }

    @Test
    void getAllUsersReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void updateUserUpdateFields() {
        User updatedUser = new User();
        updatedUser.setFirstName("Petar");
        updatedUser.setLastName("Petrov");
        updatedUser.setEmail("petar@gmail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updatedUser);

        assertEquals("Petar", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("petar@gmail.com", result.getEmail());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void deleteUserDeletesSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserThrowsWhenNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(any());
    }
}