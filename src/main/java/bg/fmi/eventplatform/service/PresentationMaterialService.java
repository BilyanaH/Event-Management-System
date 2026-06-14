package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.AgendaItem;
import bg.fmi.eventplatform.domain.PresentationMaterial;
import bg.fmi.eventplatform.domain.Speaker;
import bg.fmi.eventplatform.dto.response.PresentationMaterialResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.PresentationMaterialRepository;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PresentationMaterialService {

    private final PresentationMaterialRepository materialRepository;
    private final SpeakerRepository speakerRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final CloudinaryStorageService storageService;

    public PresentationMaterialService(PresentationMaterialRepository materialRepository,
                                       SpeakerRepository speakerRepository,
                                       AgendaItemRepository agendaItemRepository,
                                       CloudinaryStorageService storageService) {
        this.materialRepository = materialRepository;
        this.speakerRepository = speakerRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.storageService = storageService;
    }

    public PresentationMaterialResponse upload(Long speakerId, MultipartFile file, Long agendaItemId) throws IOException {
        Speaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new EntityNotFoundException("Speaker not found with id " + speakerId));

        AgendaItem agendaItem = null;
        if (agendaItemId != null) {
            agendaItem = agendaItemRepository.findById(agendaItemId)
                    .orElseThrow(() -> new EntityNotFoundException("Agenda item not found with id " + agendaItemId));
        }

        String url = storageService.upload(file, "speakers/" + speakerId);

        PresentationMaterial material = new PresentationMaterial();
        material.setSpeaker(speaker);
        material.setAgendaItem(agendaItem);
        material.setFileName(file.getOriginalFilename());
        material.setFileUrl(url);
        material.setFileType(file.getContentType());
        material.setUploadedAt(LocalDateTime.now());

        return PresentationMaterialResponse.fromEntity(materialRepository.save(material));
    }

    public List<PresentationMaterialResponse> listForSpeaker(Long speakerId) {
        if (!speakerRepository.existsById(speakerId)) {
            throw new EntityNotFoundException("Speaker not found with id " + speakerId);
        }
        return materialRepository.findBySpeakerId(speakerId).stream()
                .map(PresentationMaterialResponse::fromEntity)
                .toList();
    }
}
