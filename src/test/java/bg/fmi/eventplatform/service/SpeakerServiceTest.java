package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Speaker;
import bg.fmi.eventplatform.dto.request.SpeakerRequest;
import bg.fmi.eventplatform.dto.response.SpeakerResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeakerServiceTest {

    @Mock
    private SpeakerRepository speakerRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SpeakerService speakerService;

    private SpeakerRequest request;

    @BeforeEach
    void setUp() {
        request = new SpeakerRequest(null, "Jane Doe", "Bio", "Acme", "CTO", null, null);
    }

    @Test
    void createSpeakerSavesEntity() {
        when(speakerRepository.save(any(Speaker.class))).thenAnswer(inv -> {
            Speaker s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        SpeakerResponse response = speakerService.createSpeaker(request);

        assertEquals("Jane Doe", response.name());
        assertEquals(1L, response.id());
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(speakerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> speakerService.getById(99L));
    }
}
