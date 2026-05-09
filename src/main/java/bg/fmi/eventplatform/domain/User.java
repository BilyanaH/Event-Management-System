package bg.fmi.eventplatform.domain;

import bg.fmi.eventplatform.dto.UserRequest;
import bg.fmi.eventplatform.vo.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public User(UserRequest userRequest) {
        this.email = userRequest.email();
        this.password = userRequest.password();
        this.firstName = userRequest.firstName();
        this.lastName = userRequest.lastName();
        this.role = userRequest.role();
        this.createdAt = LocalDateTime.now();
    }

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}