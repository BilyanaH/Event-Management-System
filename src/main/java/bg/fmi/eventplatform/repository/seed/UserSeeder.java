package bg.fmi.eventplatform.repository.seed;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class UserSeeder implements CommandLineRunner {
    private final UserRepository userRepository;

    public UserSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }

        UserRequest userRequest = new UserRequest(
                "ivan01030405@gmail.com",
                "meowmeow",
                "Ivan",
                "Ivanov",
                UserRole.ORGANIZER);
        User user = new User(userRequest);
        userRepository.save(user);
    }
}
