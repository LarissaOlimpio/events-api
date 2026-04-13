package com.manager.events_api.domain.coupon;

import java.time.OffsetDateTime;

public record CouponRequestDTO(String code, OffsetDateTime valid, Integer discount) {
}
