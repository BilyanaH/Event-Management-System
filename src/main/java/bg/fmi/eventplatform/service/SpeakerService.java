package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Speaker;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.SpeakerRequest;
import bg.fmi.eventplatform.dto.response.SpeakerResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.exception.UserNotFoundException;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;
    private final UserRepository userRepository;

    public SpeakerService(SpeakerRepository speakerRepository, UserRepository userRepository) {
        this.speakerRepository = speakerRepository;
        this.userRepository = userRepository;
    }

    public SpeakerResponse createSpeaker(SpeakerRequest request) {
        User user = null;
        if (request.userId() != null) {
            user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new UserNotFoundException(request.userId()));
        }
        Speaker speaker = SpeakerRequest.toEntity(request, user);
        return SpeakerResponse.fromEntity(speakerRepository.save(speaker));
    }

    public List<SpeakerResponse> getAll() {
        return speakerRepository.findAll().stream()
                .map(SpeakerResponse::fromEntity)
                .toList();
    }

    public SpeakerResponse getById(Long id) {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Speaker not found with id " + id));
        return SpeakerResponse.fromEntity(speaker);
    }

    public SpeakerResponse updateSpeaker(Long id, SpeakerRequest request) {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Speaker not found with id " + id));

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new UserNotFoundException(request.userId()));
            speaker.setUser(user);
        }
        speaker.setName(request.name());
        speaker.setBio(request.bio());
        speaker.setCompany(request.company());
        speaker.setTitlePosition(request.titlePosition());
        speaker.setPhotoUrl(request.photoUrl());
        speaker.setWebsiteUrl(request.websiteUrl());

        return SpeakerResponse.fromEntity(speakerRepository.save(speaker));
    }

    public Speaker findEntityById(Long id) {
        return speakerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Speaker not found with id " + id));
    }
}
