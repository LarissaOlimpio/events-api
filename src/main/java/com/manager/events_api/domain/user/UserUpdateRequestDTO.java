package com.manager.events_api.domain.user;

public record UserUpdateRequestDTO(
        String name,
        String email,
        String role
) {
}