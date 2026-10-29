package com.manager.events_api.domain.user;

import jakarta.validation.constraints.NotNull;

public record UserRoleRequestDTO(
        @NotNull Role role
) {
}
