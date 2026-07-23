package com.manager.events_api.domain.event;

import com.manager.events_api.domain.address.AddressRequestDTO;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EventRequestDTO(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Date is required")
        @Future(message = "Event date must be in the future")
        OffsetDateTime date,

        @NotNull(message = "Remote status is required")

        Boolean remote,

        @NotNull(message = "Url is required")
        String eventUrl,
        
        AddressRequestDTO address) {
}
