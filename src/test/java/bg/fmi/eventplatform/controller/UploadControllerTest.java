package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.dto.response.UploadResponse;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.service.CloudinaryStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UploadControllerTest {

    @Mock
    private CloudinaryStorageService storageService;

    @InjectMocks
    private UploadController uploadController;

    @Test
    void uploadImageReturnsCreatedWithUrl() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", new byte[]{1,2,3});
        when(storageService.upload(any(MultipartFile.class), eq("images")))
                .thenReturn("https://cdn.example.com/images/photo.png");

        ResponseEntity<UploadResponse> response = uploadController.uploadImage(file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://cdn.example.com/images/photo/png", response.getBody().url());
    }

    @Test
    void uploadImageRejectsEmptyFile() {
        MultipartFile empty = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        assertThrows(ValidationException.class, () -> uploadController.uploadImage(empty));
        verifyNoInteractions(storageService);
    }

    @Test
    void uploadImageRejectsNonImageContentType() {
        MultipartFile pdf = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", new byte[] {1,2,3,4});

        assertThrows(ValidationException.class, () -> uploadController.uploadImage(pdf));
        verifyNoInteractions(storageService);
    }

    @Test
    void uploadImageRejectsOversizedFile() {
        byte[] tooBig = new byte[15 * 1024 * 1024];
        MultipartFile huge = new MockMultipartFile(
                "file", "big.jpg", "image/jpeg", tooBig);

        assertThrows(ValidationException.class, () -> uploadController.uploadImage(huge));
        verifyNoInteractions(storageService);
    }
}
