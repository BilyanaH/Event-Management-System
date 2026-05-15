package bg.fmi.eventplatform.repository.seed;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.EventCategory;
import bg.fmi.eventplatform.vo.EventStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Order(2)
@Component
public class EventSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventSeeder(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (eventRepository.count() > 0) {
            return;
        }

        User organizer = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No users found. UserSeeder must run first."));

        LocalDateTime now = LocalDateTime.now();

        Event techConference = new Event();
        techConference.setOrganizer(organizer);
        techConference.setTitle("Sofia Tech Conference 2026");
        techConference.setDescription("Annual technology conference featuring talks on AI, cloud computing, and software engineering.");
        techConference.setVenue("Sofia Event Center");
        techConference.setCity("Sofia");
        techConference.setVenueAddress("bul. Aleksandar Stamboliyski 55");
        techConference.setStartDate(now.plusDays(30));
        techConference.setEndDate(now.plusDays(31));
        techConference.setCapacity(500);
        techConference.setStatus(EventStatus.PUBLISHED);
        techConference.setCategory(EventCategory.SCIENCE_AND_TECHNOLOGY);
        techConference.setCreatedAt(now);
        techConference.setUpdatedAt(now);

        Event musicFestival = new Event();
        musicFestival.setOrganizer(organizer);
        musicFestival.setTitle("Summer Beats Festival");
        musicFestival.setDescription("Three-day outdoor music festival with live bands and DJs.");
        musicFestival.setVenue("South Park");
        musicFestival.setCity("Sofia");
        musicFestival.setVenueAddress("South Park, Main Stage");
        musicFestival.setStartDate(now.plusDays(60));
        musicFestival.setEndDate(now.plusDays(62));
        musicFestival.setCapacity(2000);
        musicFestival.setStatus(EventStatus.PUBLISHED);
        musicFestival.setCategory(EventCategory.MUSIC);
        musicFestival.setCreatedAt(now);
        musicFestival.setUpdatedAt(now);

        Event foodFair = new Event();
        foodFair.setOrganizer(organizer);
        foodFair.setTitle("Bulgarian Food & Wine Fair");
        foodFair.setDescription("Explore traditional Bulgarian cuisine and local wines from across the country.");
        foodFair.setVenue("Inter Expo Center");
        foodFair.setCity("Sofia");
        foodFair.setVenueAddress("bul. Tsarigradsko Shose 147");
        foodFair.setStartDate(now.plusDays(15));
        foodFair.setEndDate(now.plusDays(16));
        foodFair.setCapacity(300);
        foodFair.setStatus(EventStatus.PUBLISHED);
        foodFair.setCategory(EventCategory.FOOD_AND_DRINK);
        foodFair.setCreatedAt(now);
        foodFair.setUpdatedAt(now);

        Event businessMeetup = new Event();
        businessMeetup.setOrganizer(organizer);
        businessMeetup.setTitle("Startup Networking Meetup");
        businessMeetup.setDescription("Monthly networking event for entrepreneurs and investors in the Bulgarian startup ecosystem.");
        businessMeetup.setVenue("Puzl CowOrKing");
        businessMeetup.setCity("Sofia");
        businessMeetup.setVenueAddress("bul. Alexander Malinov 51");
        businessMeetup.setStartDate(now.plusDays(7));
        businessMeetup.setEndDate(now.plusDays(7).plusHours(3));
        businessMeetup.setCapacity(100);
        businessMeetup.setStatus(EventStatus.PUBLISHED);
        businessMeetup.setCategory(EventCategory.BUSINESS);
        businessMeetup.setCreatedAt(now);
        businessMeetup.setUpdatedAt(now);

        Event completedEvent = new Event();
        completedEvent.setOrganizer(organizer);
        completedEvent.setTitle("Spring Art Exhibition");
        completedEvent.setDescription("Exhibition showcasing works from emerging Bulgarian artists.");
        completedEvent.setVenue("National Gallery");
        completedEvent.setCity("Sofia");
        completedEvent.setVenueAddress("pl. Knyaz Alexander I 1");
        completedEvent.setStartDate(now.minusDays(10));
        completedEvent.setEndDate(now.minusDays(8));
        completedEvent.setCapacity(150);
        completedEvent.setStatus(EventStatus.COMPLETED);
        completedEvent.setCategory(EventCategory.PERFORMING_AND_VISUAL_ARTS);
        completedEvent.setCreatedAt(now.minusDays(30));
        completedEvent.setUpdatedAt(now.minusDays(8));

        eventRepository.save(techConference);
        eventRepository.save(musicFestival);
        eventRepository.save(foodFair);
        eventRepository.save(businessMeetup);
        eventRepository.save(completedEvent);
    }
}