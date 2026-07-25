package com.manager.events_api.domain.address;

import jakarta.validation.constraints.Size;

public record AddressRequestDTO(
        @Size(min = 3, max = 100, message = "city must be between 3 and 100 characters")
        String city,
        @Size(max = 2, message = "uf must be 2 characters")
        String uf) {
}
