package com.manager.events_api.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventResponseDTO(
        UUID id,
        String title,
        String description,
        String eventUrl,
        Boolean remote,
        OffsetDateTime date
) {
}
