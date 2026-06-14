package bg.fmi.eventplatform.repository.seed;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.vo.TicketStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Order(4)
@Component
@Profile("!test")
public class TicketSeeder implements CommandLineRunner {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public TicketSeeder(TicketRepository ticketRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) {
        if (ticketRepository.count() > 0) {
            return;
        }

        List<Event> events = eventRepository.findAll();
        for (Event event : events) {
            Ticket standard = new Ticket();
            standard.setEvent(event);
            standard.setName("Standard");
            standard.setDescription("General admission");
            standard.setPrice(BigDecimal.valueOf(20));
            standard.setQuantityAvailable(50);
            standard.setQuantitySold(0);
            standard.setStatus(TicketStatus.AVAILABLE);

            Ticket vip = new Ticket();
            vip.setEvent(event);
            vip.setName("VIP");
            vip.setDescription("Premium seating, swag bag");
            vip.setPrice(BigDecimal.valueOf(75));
            vip.setQuantityAvailable(10);
            vip.setQuantitySold(0);
            vip.setStatus(TicketStatus.AVAILABLE);

            ticketRepository.save(standard);
            ticketRepository.save(vip);
        }
    }
}
