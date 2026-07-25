package com.manager.events_api.domain.coupon;

import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public record CouponRequestDTO(
        @NotBlank(message = "Code is required")
        @Size(min = 3, max = 255, message = "Code must be between 3 and 100 characters")
        String code,
        @NotNull(message = "validFrom is required")
        OffsetDateTime validFrom,
        @NotNull(message = "validUntil is required")
        @Future(message = "validUntil must be in the future")
        OffsetDateTime validUntil,
        @NotNull(message = "discount is required")
        @Positive
        Integer discount
) {
}
