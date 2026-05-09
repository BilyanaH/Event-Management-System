package bg.fmi.eventplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback", indexes = {
        @Index(name = "idx_feedback_user_event", columnList = "user_id, event_id")
        // added these indices to look up faster
})
@Getter
@Setter
@NoArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "venue_rating")
    private Integer venueRating;

    @Column(name = "content_rating")
    private Integer contentRating;

    @Column(name = "organization_rating")
    private Integer organizationRating;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;
}