package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Registration;
import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.RegistrationRequest;
import bg.fmi.eventplatform.dto.response.RegistrationResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import bg.fmi.eventplatform.vo.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private static final String CONFIRMATION_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CONFIRMATION_CODE_LENGTH = 12;

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public RegistrationService(RegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               TicketRepository ticketRepository,
                               EmailService emailService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.emailService = emailService;
    }

    @Transactional
    public RegistrationResponse register(Long eventId, RegistrationRequest request, User principal) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + eventId));
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new ValidationException("Cannot register for event with status " + event.getStatus());
        }

        Ticket ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id " + request.ticketId()));
        if (!ticket.getEvent().getId().equals(eventId)) {
            throw new ValidationException("Ticket does not belong to event " + eventId);
        }
        if (ticket.getQuantitySold() >= ticket.getQuantityAvailable()) {
            throw new ValidationException("Ticket is sold out");
        }
        if (ticket.getSaleEnd() != null && ticket.getSaleEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Ticket sale has ended");
        }

        if (event.getCapacity() != null) {
            long active = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED)
                    + registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CHECKED_IN);
            if (active >= event.getCapacity()) {
                throw new ValidationException("Event capacity reached");
            }
        }

        if (registrationRepository.existsByUserIdAndEventIdAndStatus(principal.getId(), eventId, RegistrationStatus.CONFIRMED)
                || registrationRepository.existsByUserIdAndEventIdAndStatus(principal.getId(), eventId, RegistrationStatus.CHECKED_IN)) {
            throw new ValidationException("User already has an active registration for this event");
        }

        Registration registration = new Registration();
        registration.setUser(principal);
        registration.setEvent(event);
        registration.setTicket(ticket);
        registration.setStatus(RegistrationStatus.CONFIRMED);
        registration.setConfirmationCode(generateConfirmationCode());
        registration.setRegisteredAt(LocalDateTime.now());

        ticket.setQuantitySold(ticket.getQuantitySold() + 1);
        if (ticket.getQuantitySold() >= ticket.getQuantityAvailable()) {
            ticket.setStatus(TicketStatus.SOLD_OUT);
        }
        ticketRepository.save(ticket);

        Registration saved = registrationRepository.save(registration);
        emailService.sendRegistrationConfirmation(saved);
        return RegistrationResponse.fromEntity(saved);
    }

    public List<RegistrationResponse> listForEvent(Long eventId, User principal) throws AccessDeniedException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + eventId));
        if (!event.getOrganizer().getId().equals(principal.getId())) {
            throw new AccessDeniedException("User is not the organizer of this event");
        }
        return registrationRepository.findByEventId(eventId).stream()
                .map(RegistrationResponse::fromEntity)
                .toList();
    }

    public RegistrationResponse getById(Long id, User principal) throws AccessDeniedException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found with id " + id));
        boolean isOwner = registration.getUser().getId().equals(principal.getId());
        boolean isOrganizer = registration.getEvent().getOrganizer().getId().equals(principal.getId());
        if (!isOwner && !isOrganizer) {
            throw new AccessDeniedException("User does not have access to this registration");
        }
        return RegistrationResponse.fromEntity(registration);
    }

    @Transactional
    public RegistrationResponse cancel(Long id, User principal) throws AccessDeniedException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found with id " + id));
        if (!registration.getUser().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Only the owner can cancel this registration");
        }
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            return RegistrationResponse.fromEntity(registration);
        }
        if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
            throw new ValidationException("Cannot cancel a registration that has already been checked in");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);

        Ticket ticket = registration.getTicket();
        if (ticket.getQuantitySold() != null && ticket.getQuantitySold() > 0) {
            ticket.setQuantitySold(ticket.getQuantitySold() - 1);
        }
        if (ticket.getStatus() == TicketStatus.SOLD_OUT && ticket.getQuantitySold() < ticket.getQuantityAvailable()) {
            ticket.setStatus(TicketStatus.AVAILABLE);
        }
        ticketRepository.save(ticket);

        Registration saved = registrationRepository.save(registration);
        emailService.sendCancellationConfirmation(saved);
        return RegistrationResponse.fromEntity(saved);
    }

    @Transactional
    public RegistrationResponse checkIn(Long id, User principal) throws AccessDeniedException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found with id " + id));
        if (!registration.getEvent().getOrganizer().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Only the event organizer can check in attendees");
        }
        if (registration.getStatus() != RegistrationStatus.CONFIRMED) {
            throw new ValidationException("Only CONFIRMED registrations can be checked in");
        }
        registration.setStatus(RegistrationStatus.CHECKED_IN);
        registration.setCheckedInAt(LocalDateTime.now());
        Registration saved = registrationRepository.save(registration);
        emailService.sendCheckInConfirmation(saved);
        return RegistrationResponse.fromEntity(saved);
    }

    public Page<RegistrationResponse> listMine(User principal, Pageable pageable) {
        return registrationRepository.findByUserId(principal.getId(), pageable)
                .map(RegistrationResponse::fromEntity);
    }

    private String generateConfirmationCode() {
        StringBuilder sb = new StringBuilder(CONFIRMATION_CODE_LENGTH);
        for (int i = 0; i < CONFIRMATION_CODE_LENGTH; i++) {
            sb.append(CONFIRMATION_ALPHABET.charAt(secureRandom.nextInt(CONFIRMATION_ALPHABET.length())));
        }
        return sb.toString();
    }
}
