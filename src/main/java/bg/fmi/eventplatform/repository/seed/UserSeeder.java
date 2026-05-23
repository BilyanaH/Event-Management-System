package bg.fmi.eventplatform.repository.seed;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class UserSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }

        UserRequest userRequest = new UserRequest(
                "meow@gmail.com",
                "meowmeow",
                "Ivan",
                "Ivanov",
                UserRole.ORGANIZER);
        User user = new User(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        userRepository.save(user);
    }
}
