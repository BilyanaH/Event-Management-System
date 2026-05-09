package bg.fmi.eventplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "speakers")
@Getter
@Setter
@NoArgsConstructor
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 200)
    private String company;

    @Column(name = "title_position", length = 200)
    private String titlePosition;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;
}