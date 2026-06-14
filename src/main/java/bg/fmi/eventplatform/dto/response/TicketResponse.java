package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.vo.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        Long eventId,
        String name,
        String description,
        BigDecimal price,
        Integer quantityAvailable,
        Integer quantitySold,
        LocalDateTime saleStart,
        LocalDateTime saleEnd,
        TicketStatus status
) {
    public static TicketResponse fromEntity(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getEvent().getId(),
                ticket.getName(),
                ticket.getDescription(),
                ticket.getPrice(),
                ticket.getQuantityAvailable(),
                ticket.getQuantitySold(),
                ticket.getSaleStart(),
                ticket.getSaleEnd(),
                ticket.getStatus()
        );
    }
}
