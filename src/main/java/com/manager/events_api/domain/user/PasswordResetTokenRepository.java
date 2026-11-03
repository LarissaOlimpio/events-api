package com.manager.events_api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    @Modifying
    @Query("update PasswordResetToken token set token.usedAt = :now where token.user.id = :userId and token.usedAt is null")
    void invalidatePendingForUser(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
