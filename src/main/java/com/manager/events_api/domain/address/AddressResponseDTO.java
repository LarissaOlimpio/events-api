package com.manager.events_api.domain.address;

import java.util.UUID;

public record AddressResponseDTO(UUID id, String city, String uf) {
}
