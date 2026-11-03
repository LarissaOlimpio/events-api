package com.manager.events_api.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank String token,
        @NotBlank @Size(min = 8) String newPassword) {
}
