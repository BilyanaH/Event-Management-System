package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.dto.response.UploadResponse;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.service.CloudinaryStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/uploads")
@Tag(name = "Uploads App")
public class UploadController {

    private static final long MAX_IMAGE_BYTES = 10L * 1024 * 1024; // 10 MB
    private static final String IMAGE_FOLDER = "images";

    private final CloudinaryStorageService storageService;

    @Autowired
    public UploadController(CloudinaryStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an image, return a hosted URL")
    public ResponseEntity<UploadResponse> uploadImage(@RequestPart("file")MultipartFile file) throws IOException {
        validateImage(file);
        String url = storageService.upload(file, IMAGE_FOLDER);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UploadResponse(url));
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("Only image files are allowed");
        }
        if(file.getSize() > MAX_IMAGE_BYTES) {
            throw new ValidationException("Max image size is 10MB");
        }
    }
}
