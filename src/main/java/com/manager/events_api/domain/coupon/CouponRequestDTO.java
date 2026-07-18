package com.manager.events_api.domain.coupon;

import java.time.OffsetDateTime;

public record CouponRequestDTO(String code, OffsetDateTime validFrom, OffsetDateTime validUntil, Integer discount) {
}
