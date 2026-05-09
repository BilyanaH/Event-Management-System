package bg.fmi.eventplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_analytics")
@Getter
@Setter
@NoArgsConstructor
public class EventAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "total_registrations", nullable = false)
    private Integer totalRegistrations = 0;

    @Column(name = "total_check_ins", nullable = false)
    private Integer totalCheckIns = 0;

    @Column(name = "total_cancellations", nullable = false)
    private Integer totalCancellations = 0;

    @Column(name = "total_feedback", nullable = false)
    private Integer totalFeedback = 0;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating;

    @Column(precision = 12, scale = 2)
    private BigDecimal revenue;

    @Column(columnDefinition = "TEXT")
    private String demographics;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;
}