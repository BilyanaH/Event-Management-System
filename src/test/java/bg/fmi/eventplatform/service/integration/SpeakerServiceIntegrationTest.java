package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.dto.request.SpeakerRequest;
import bg.fmi.eventplatform.dto.response.SpeakerResponse;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import bg.fmi.eventplatform.service.SpeakerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SpeakerServiceIntegrationTest {

    @Autowired
    private SpeakerService speakerService;
    @Autowired
    private SpeakerRepository speakerRepository;

    @BeforeEach
    void setUp() {
        speakerRepository.deleteAll();
    }

    @Test
    void createAndFetchSpeaker() {
        SpeakerResponse created = speakerService.createSpeaker(
                new SpeakerRequest(null, "Jane Doe", "bio", "Acme", "CTO", null, null));

        SpeakerResponse fetched = speakerService.getById(created.id());

        assertEquals("Jane Doe", fetched.name());
    }

    @Test
    void updateSpeakerChangesFields() {
        SpeakerResponse created = speakerService.createSpeaker(
                new SpeakerRequest(null, "Jane Doe", "bio", "Acme", "CTO", null, null));

        SpeakerResponse updated = speakerService.updateSpeaker(created.id(),
                new SpeakerRequest(null, "Jane Smith", "new bio", null, null, null, null));

        assertEquals("Jane Smith", updated.name());
    }
}
