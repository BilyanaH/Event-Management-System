package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.PasswordResetToken;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.repository.PasswordResetTokenRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

@Service
public class
PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            tokenRepository.deleteByUserId(user.getId());

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(generateToken());
            resetToken.setExpiresAt(LocalDateTime.now(ZoneId.of("UTC")).plusHours(1));
            tokenRepository.save(resetToken);

            String resetLink = frontendUrl + "/reset-password?token=" + resetToken.getToken();
            emailService.sendPasswordResetEmail(user, resetLink);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid or expired reset token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new ValidationException("Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
