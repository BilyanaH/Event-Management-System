package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.LoginRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.AuthResponse;
import bg.fmi.eventplatform.dto.response.UserResponse;
import bg.fmi.eventplatform.exception.InvalidCredentialsException;
import bg.fmi.eventplatform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(UserRequest request) {
        User user = userService.createUser(request);
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, UserResponse.fromEntity(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, UserResponse.fromEntity(user));
    }

    public void logout(String token) {
        jwtService.invalidate(token);
    }
}
