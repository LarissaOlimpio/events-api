package com.manager.events_api.domain.user;

import com.manager.events_api.infra.email.EmailSender;
import com.manager.events_api.infra.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {
    private static final String RESET_SUBJECT = "Password reset";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid or expired token";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final long expirationMinutes;
    private final String frontendUrl;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailSender emailSender,
                                @Value("${app.password-reset.expiration-minutes:30}") long expirationMinutes,
                                @Value("${app.frontend-url:http://localhost:3000}") String frontendUrl) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.expirationMinutes = expirationMinutes;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            LocalDateTime now = LocalDateTime.now();
            tokenRepository.invalidatePendingForUser(user.getId(), now);
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            tokenRepository.save(new PasswordResetToken(user, hashToken(rawToken), now.plusMinutes(expirationMinutes)));
            String resetUrl = frontendUrl + "/reset-password?token=" + rawToken;
            emailSender.send(user.getEmail(), RESET_SUBJECT, resetUrl);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        PasswordResetToken token = tokenRepository.findByTokenHash(hashToken(request.token()))
                .orElseThrow(() -> invalidToken());
        LocalDateTime now = LocalDateTime.now();
        if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(now)) {
            throw invalidToken();
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        token.setUsedAt(now);
        userRepository.save(user);
        tokenRepository.save(token);
    }

    private BusinessException invalidToken() {
        return new BusinessException(INVALID_TOKEN_MESSAGE);
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormatHolder.toHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private static final class HexFormatHolder {
        private static String toHex(byte[] bytes) {
            return java.util.HexFormat.of().formatHex(bytes);
        }
    }
}
