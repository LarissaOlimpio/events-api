package com.manager.events_api.domain.event;

import com.manager.events_api.domain.address.AddressResponseDTO;
import com.manager.events_api.domain.coupon.CouponResponseDTO;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EventDetailsDTO(UUID id,
                              String title,
                              String description,
                              String eventUrl,
                              Boolean remote,
                              OffsetDateTime date,
                              AddressResponseDTO address,
                              List<CouponResponseDTO> coupons) {
}
