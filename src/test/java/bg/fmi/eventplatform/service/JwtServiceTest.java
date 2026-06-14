package bg.fmi.eventplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-must-be-at-least-32-bytes-long-aaaa", 3600000L);
    }

    @Test
    void generateAndExtractEmail() {
        String token = jwtService.generateToken("ivan@gmail.com", "ATTENDEE");

        assertNotNull(token);
        assertEquals("ivan@gmail.com", jwtService.extractEmail(token));
    }

    @Test
    void isValidReturnsTrueForFreshToken() {
        String token = jwtService.generateToken("ivan@gmail.com", "ATTENDEE");

        assertTrue(jwtService.isValid(token));
    }

    @Test
    void invalidateBlacklistsToken() {
        String token = jwtService.generateToken("ivan@gmail.com", "ATTENDEE");

        jwtService.invalidate(token);

        assertFalse(jwtService.isValid(token));
    }

    @Test
    void isValidReturnsFalseForGarbageToken() {
        assertFalse(jwtService.isValid("not-a-real-token"));
    }
}
