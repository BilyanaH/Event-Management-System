package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.TicketRequest;
import bg.fmi.eventplatform.dto.response.TicketResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.vo.TicketStatus;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    public TicketResponse createTicket(Long eventId, TicketRequest request, User principal) throws AccessDeniedException {
        Event event = loadEventForOrganizer(eventId, principal);
        Ticket ticket = TicketRequest.toEntity(request, event);
        applyDerivedStatus(ticket);
        return TicketResponse.fromEntity(ticketRepository.save(ticket));
    }

    public List<TicketResponse> getTicketsForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found with id " + eventId);
        }
        return ticketRepository.findByEventId(eventId).stream()
                .map(TicketResponse::fromEntity)
                .toList();
    }

    public TicketResponse getTicket(Long eventId, Long ticketId) {
        Ticket ticket = loadTicketForEvent(eventId, ticketId);
        return TicketResponse.fromEntity(ticket);
    }

    public TicketResponse updateTicket(Long eventId, Long ticketId, TicketRequest request, User principal) throws AccessDeniedException {
        loadEventForOrganizer(eventId, principal);
        Ticket ticket = loadTicketForEvent(eventId, ticketId);

        ticket.setName(request.name());
        ticket.setDescription(request.description());
        ticket.setPrice(request.price());
        ticket.setQuantityAvailable(request.quantityAvailable());
        ticket.setSaleStart(request.saleStart());
        ticket.setSaleEnd(request.saleEnd());
        applyDerivedStatus(ticket);
        return TicketResponse.fromEntity(ticketRepository.save(ticket));
    }

    public void deleteTicket(Long eventId, Long ticketId, User principal) throws AccessDeniedException {
        loadEventForOrganizer(eventId, principal);
        Ticket ticket = loadTicketForEvent(eventId, ticketId);
        ticketRepository.delete(ticket);
    }

    void applyDerivedStatus(Ticket ticket) {
        if (ticket.getSaleEnd() != null && ticket.getSaleEnd().isBefore(LocalDateTime.now())) {
            ticket.setStatus(TicketStatus.SALE_ENDED);
        } else if (ticket.getQuantitySold() != null && ticket.getQuantitySold() >= ticket.getQuantityAvailable()) {
            ticket.setStatus(TicketStatus.SOLD_OUT);
        } else {
            ticket.setStatus(TicketStatus.AVAILABLE);
        }
    }

    private Event loadEventForOrganizer(Long eventId, User principal) throws AccessDeniedException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + eventId));
        if (!event.getOrganizer().getId().equals(principal.getId())) {
            throw new AccessDeniedException("User is not the organizer of this event");
        }
        return event;
    }

    private Ticket loadTicketForEvent(Long eventId, Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id " + ticketId));
        if (!ticket.getEvent().getId().equals(eventId)) {
            throw new EntityNotFoundException("Ticket " + ticketId + " does not belong to event " + eventId);
        }
        return ticket;
    }
}
