package bg.fmi.eventplatform.repository.seed;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Feedback;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.EventStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Order(7)
@Component
@Profile("!test")
public class FeedbackSeeder implements CommandLineRunner {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public FeedbackSeeder(FeedbackRepository feedbackRepository,
                          UserRepository userRepository,
                          EventRepository eventRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) {
        if (feedbackRepository.count() > 0) {
            return;
        }
        Optional<Event> completed = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.COMPLETED)
                .findFirst();
        Optional<User> reviewer = userRepository.findAll().stream().findFirst();
        if (completed.isEmpty() || reviewer.isEmpty()) return;

        Feedback feedback = new Feedback();
        feedback.setEvent(completed.get());
        feedback.setUser(reviewer.get());
        feedback.setOverallRating(5);
        feedback.setVenueRating(5);
        feedback.setContentRating(4);
        feedback.setOrganizationRating(5);
        feedback.setComment("Beautifully curated, would attend again.");
        feedback.setSubmittedAt(LocalDateTime.now().minusDays(7));

        feedbackRepository.save(feedback);
    }
}
