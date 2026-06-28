package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.dto.request.ForgotPasswordRequest;
import bg.fmi.eventplatform.dto.request.LoginRequest;
import bg.fmi.eventplatform.dto.request.ResetPasswordRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.AuthResponse;
import bg.fmi.eventplatform.service.AuthService;
import bg.fmi.eventplatform.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Api")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user and issue a JWT")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(userRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "Login, returns JWT")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send password reset email")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        passwordResetService.requestReset(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token from email")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate the current JWT (process-local blacklist)")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            authService.logout(header.substring(BEARER_PREFIX.length()));
        }
        return ResponseEntity.noContent().build();
    }
}
