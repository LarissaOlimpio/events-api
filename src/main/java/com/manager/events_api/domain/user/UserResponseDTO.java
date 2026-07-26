package com.manager.events_api.domain.user;

public record UserResponseDTO(
        String name,
        String email,
        String role
) {
}
