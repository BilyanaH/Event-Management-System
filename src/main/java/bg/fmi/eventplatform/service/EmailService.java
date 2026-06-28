package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Registration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:hello@demomailtrap.co}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendRegistrationConfirmation(Registration registration) {
        String subject = "You're registered: " + registration.getEvent().getTitle();
        String body = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h2>You're in! 🎉</h2>
                    <p>Hi %s,</p>
                    <p>Your registration for <b>%s</b> has been confirmed.</p>
                    <table style="width:100%%; border-collapse: collapse; margin: 20px 0;">
                        <tr><td style="padding: 8px; color: #666;">Date</td><td style="padding: 8px;"><b>%s</b></td></tr>
                        <tr><td style="padding: 8px; color: #666;">Venue</td><td style="padding: 8px;"><b>%s</b></td></tr>
                        <tr><td style="padding: 8px; color: #666;">Ticket</td><td style="padding: 8px;"><b>%s</b></td></tr>
                        <tr><td style="padding: 8px; color: #666;">Confirmation code</td><td style="padding: 8px;"><b style="font-size: 18px; letter-spacing: 2px;">%s</b></td></tr>
                    </table>
                    <p style="color: #666; font-size: 13px;">Keep your confirmation code handy — you'll need it to check in at the event.</p>
                </div>
                """.formatted(
                registration.getUser().getFirstName(),
                registration.getEvent().getTitle(),
                registration.getEvent().getStartDate().format(FORMATTER),
                registration.getEvent().getVenue() != null ? registration.getEvent().getVenue() : "TBA",
                registration.getTicket().getName(),
                registration.getConfirmationCode()
        );
        send(registration.getUser().getEmail(), subject, body);
    }

    @Async
    public void sendCancellationConfirmation(Registration registration) {
        String subject = "Registration cancelled: " + registration.getEvent().getTitle();
        String body = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h2>Registration Cancelled</h2>
                    <p>Hi %s,</p>
                    <p>Your registration for <b>%s</b> on <b>%s</b> has been cancelled.</p>
                    <p>If this was a mistake, you can register again as long as tickets are still available.</p>
                </div>
                """.formatted(
                registration.getUser().getFirstName(),
                registration.getEvent().getTitle(),
                registration.getEvent().getStartDate().format(FORMATTER)
        );
        send(registration.getUser().getEmail(), subject, body);
    }

    @Async
    public void sendCheckInConfirmation(Registration registration) {
        String subject = "Checked in: " + registration.getEvent().getTitle();
        String body = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h2>You're checked in! ✅</h2>
                    <p>Hi %s,</p>
                    <p>You've been successfully checked in to <b>%s</b>. Enjoy the event!</p>
                    <p style="color: #666; font-size: 13px;">Checked in at: %s</p>
                </div>
                """.formatted(
                registration.getUser().getFirstName(),
                registration.getEvent().getTitle(),
                registration.getCheckedInAt().format(FORMATTER)
        );
        send(registration.getUser().getEmail(), subject, body);
    }

    public void sendPasswordResetEmail(bg.fmi.eventplatform.domain.User user, String resetLink) {
        String subject = "Reset your password";
        String body = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h2>Password Reset</h2>
                    <p>Hi %s,</p>
                    <p>We received a request to reset your password. Click the button below to choose a new one.</p>
                    <p style="margin: 30px 0;">
                        <a href="%s" style="background-color: #4F46E5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">
                            Reset Password
                        </a>
                    </p>
                    <p style="color: #666; font-size: 13px;">This link expires in 1 hour. If you didn't request a password reset, you can ignore this email.</p>
                </div>
                """.formatted(user.getFirstName(), resetLink);
        send(user.getEmail(), subject, body);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }
}
