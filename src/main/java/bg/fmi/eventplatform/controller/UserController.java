package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.UserUpdateRequest;
import bg.fmi.eventplatform.dto.response.UserResponse;
import bg.fmi.eventplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Users Api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(UserResponse.fromEntity(principal));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateMe(@RequestBody @Valid UserUpdateRequest request,
                                                 @AuthenticationPrincipal User principal) {
        User updated = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(UserResponse.fromEntity(updated));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id (admin)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.fromEntity(userService.getUserById(id)));
    }

    @GetMapping
    @Operation(summary = "List users (admin, paginated)")
    public ResponseEntity<Page<UserResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllUsers(pageable).map(UserResponse::fromEntity));
    }
}
