package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.dto.request.SpeakerRequest;
import bg.fmi.eventplatform.dto.response.PresentationMaterialResponse;
import bg.fmi.eventplatform.dto.response.SpeakerResponse;
import bg.fmi.eventplatform.service.PresentationMaterialService;
import bg.fmi.eventplatform.service.SpeakerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/speakers")
@Tag(name = "Speakers Api")
public class SpeakerController {

    private final SpeakerService speakerService;
    private final PresentationMaterialService materialService;

    public SpeakerController(SpeakerService speakerService, PresentationMaterialService materialService) {
        this.speakerService = speakerService;
        this.materialService = materialService;
    }

    @PostMapping
    @Operation(summary = "Create speaker profile")
    public ResponseEntity<SpeakerResponse> createSpeaker(@RequestBody @Valid SpeakerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(speakerService.createSpeaker(request));
    }

    @GetMapping
    @Operation(summary = "List speakers")
    public ResponseEntity<List<SpeakerResponse>> listSpeakers() {
        return ResponseEntity.ok(speakerService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get speaker details")
    public ResponseEntity<SpeakerResponse> getSpeaker(@PathVariable Long id) {
        return ResponseEntity.ok(speakerService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update speaker")
    public ResponseEntity<SpeakerResponse> updateSpeaker(@PathVariable Long id,
                                                         @RequestBody @Valid SpeakerRequest request) {
        return ResponseEntity.ok(speakerService.updateSpeaker(id, request));
    }

    @PostMapping(value = "/{id}/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload presentation file")
    public ResponseEntity<PresentationMaterialResponse> uploadMaterial(@PathVariable Long id,
                                                                       @RequestPart("file") MultipartFile file,
                                                                       @RequestParam(required = false) Long agendaItemId) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(materialService.upload(id, file, agendaItemId));
    }

    @GetMapping("/{id}/materials")
    @Operation(summary = "List materials for speaker")
    public ResponseEntity<List<PresentationMaterialResponse>> listMaterials(@PathVariable Long id) {
        return ResponseEntity.ok(materialService.listForSpeaker(id));
    }
}
