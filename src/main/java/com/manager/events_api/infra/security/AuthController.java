package com.manager.events_api.infra.security;

import com.manager.events_api.domain.user.ForgotPasswordRequestDTO;
import com.manager.events_api.domain.user.PasswordResetService;
import com.manager.events_api.domain.user.ResetPasswordRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService,
                          PasswordResetService passwordResetService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO request) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.accepted().body("If an account exists for this email, password reset instructions have been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            return ResponseEntity.ok(new TokenResponse(tokenService.generateToken((UserDetails) authentication.getPrincipal())));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public record LoginRequest(String email, String password) {
    }

    public record TokenResponse(String token) {
    }
}
