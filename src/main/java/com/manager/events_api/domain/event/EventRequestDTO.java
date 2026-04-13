package com.manager.events_api.domain.event;

import com.manager.events_api.domain.address.AddressRequestDTO;

import java.time.OffsetDateTime;


public record EventRequestDTO(String title, String description, OffsetDateTime date, String city, String uf,
                              Boolean remote,
                              String eventUrl, AddressRequestDTO address) {
}
