package com.manager.events_api.domain.user;

import com.manager.events_api.infra.email.EmailSender;
import com.manager.events_api.infra.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailSender emailSender;

    private PasswordResetService service;

    @BeforeEach
    void setUp() {
        service = new PasswordResetService(userRepository, tokenRepository, passwordEncoder, emailSender,
                30, "http://localhost:3000");
    }

    @Test
    void forgotPasswordStoresOnlyHashAndSendsLinkForExistingEmail() throws Exception {
        User user = user();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        service.forgotPassword(new ForgotPasswordRequestDTO(user.getEmail()));

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(eq(user.getEmail()), anyString(), bodyCaptor.capture());
        String hash = tokenCaptor.getValue().getTokenHash();
        assertEquals(64, hash.length());
        String rawToken = bodyCaptor.getValue().split("token=")[1];
        assertEquals(hash(rawToken), hash);
        assertEquals(32, java.util.Base64.getUrlDecoder().decode(rawToken).length);
        verify(tokenRepository).invalidatePendingForUser(eq(user.getId()), any(LocalDateTime.class));
    }

    @Test
    void forgotPasswordDoesNothingForUnknownEmail() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        service.forgotPassword(new ForgotPasswordRequestDTO("missing@example.com"));

        verify(tokenRepository, never()).save(any());
        verify(tokenRepository, never()).invalidatePendingForUser(any(), any());
        verifyNoInteractions(emailSender);
    }

    @Test
    void resetPasswordChangesPasswordAndMarksTokenUsed() throws Exception {
        String rawToken = "reset-token-value";
        User user = user();
        PasswordResetToken token = new PasswordResetToken(user, hash(rawToken), LocalDateTime.now().plusMinutes(5));
        when(tokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");

        service.resetPassword(new ResetPasswordRequestDTO(rawToken, "new-password"));

        assertEquals("encoded-password", user.getPassword());
        assertNotNull(token.getUsedAt());
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void resetPasswordRejectsMissingExpiredAndUsedTokensWithoutChangingPassword() throws Exception {
        User user = user();
        String missing = "missing-token";
        when(tokenRepository.findByTokenHash(hash(missing))).thenReturn(Optional.empty());
        assertInvalidWithoutChangingUser(missing, user);

        String expired = "expired-token";
        when(tokenRepository.findByTokenHash(hash(expired))).thenReturn(Optional.of(
                new PasswordResetToken(user, hash(expired), LocalDateTime.now().minusSeconds(1))));
        assertInvalidWithoutChangingUser(expired, user);

        String used = "used-token";
        PasswordResetToken usedToken = new PasswordResetToken(user, hash(used), LocalDateTime.now().plusMinutes(5));
        usedToken.setUsedAt(LocalDateTime.now().minusSeconds(1));
        when(tokenRepository.findByTokenHash(hash(used))).thenReturn(Optional.of(usedToken));
        assertInvalidWithoutChangingUser(used, user);

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }

    private void assertInvalidWithoutChangingUser(String token, User user) {
        String oldPassword = user.getPassword();
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.resetPassword(new ResetPasswordRequestDTO(token, "new-password")));
        assertEquals("Invalid or expired token", exception.getMessage());
        assertEquals(oldPassword, user.getPassword());
    }

    private User user() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setPassword("old-hash");
        user.setRole(Role.VIEWER);
        return user;
    }

    private static String hash(String token) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8)));
    }
}
