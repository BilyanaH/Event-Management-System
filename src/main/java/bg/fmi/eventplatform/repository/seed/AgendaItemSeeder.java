package bg.fmi.eventplatform.repository.seed;

import bg.fmi.eventplatform.domain.AgendaItem;
import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Speaker;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import bg.fmi.eventplatform.vo.AgendaItemType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Order(5)
@Component
@Profile("!test")
public class AgendaItemSeeder implements CommandLineRunner {

    private final AgendaItemRepository agendaItemRepository;
    private final EventRepository eventRepository;
    private final SpeakerRepository speakerRepository;

    public AgendaItemSeeder(AgendaItemRepository agendaItemRepository,
                            EventRepository eventRepository,
                            SpeakerRepository speakerRepository) {
        this.agendaItemRepository = agendaItemRepository;
        this.eventRepository = eventRepository;
        this.speakerRepository = speakerRepository;
    }

    @Override
    public void run(String... args) {
        if (agendaItemRepository.count() > 0) {
            return;
        }
        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) return;

        Event event = events.get(0);
        Speaker speaker = speakerRepository.findAll().stream().findFirst().orElse(null);

        AgendaItem opening = new AgendaItem();
        opening.setEvent(event);
        opening.setSpeaker(speaker);
        opening.setTitle("Opening keynote");
        opening.setDescription("Welcome to the event");
        opening.setStartTime(event.getStartDate());
        opening.setEndTime(event.getStartDate().plusHours(1));
        opening.setLocationRoom("Hall A");
        opening.setOrderIndex(0);
        opening.setType(AgendaItemType.OPENING_SPEECH);

        AgendaItem coffee = new AgendaItem();
        coffee.setEvent(event);
        coffee.setTitle("Coffee break");
        coffee.setStartTime(event.getStartDate().plusHours(1));
        coffee.setEndTime(event.getStartDate().plusHours(1).plusMinutes(30));
        coffee.setOrderIndex(1);
        coffee.setType(AgendaItemType.BREAK);

        AgendaItem closing = new AgendaItem();
        closing.setEvent(event);
        closing.setTitle("Closing remarks");
        closing.setStartTime(event.getEndDate().minusHours(1));
        closing.setEndTime(event.getEndDate());
        closing.setOrderIndex(2);
        closing.setType(AgendaItemType.CEREMONY);

        agendaItemRepository.save(opening);
        agendaItemRepository.save(coffee);
        agendaItemRepository.save(closing);
    }
}
