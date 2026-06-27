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
import java.util.List;
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
        List<User> users = userRepository.findAll();
        if (completed.isEmpty() || users.isEmpty()) return;

        Event event = completed.get();

        Object[][] entries = {
            { users.get(0), 5, 5, 4, 5, "Beautifully curated, would attend again." },
            { users.size() > 1 ? users.get(1) : users.get(0), 3, 4, 3, 2, "Content was good but the venue was too crowded and the schedule ran late." },
            { users.size() > 2 ? users.get(2) : users.get(0), 4, 3, 5, 4, "Great speakers and topics. Registration process was a bit slow." }
        };

        for (Object[] entry : entries) {
            Feedback f = new Feedback();
            f.setEvent(event);
            f.setUser((User) entry[0]);
            f.setOverallRating((Integer) entry[1]);
            f.setVenueRating((Integer) entry[2]);
            f.setContentRating((Integer) entry[3]);
            f.setOrganizationRating((Integer) entry[4]);
            f.setComment((String) entry[5]);
            f.setSubmittedAt(LocalDateTime.now().minusDays(7));
            feedbackRepository.save(f);
        }
    }
}
