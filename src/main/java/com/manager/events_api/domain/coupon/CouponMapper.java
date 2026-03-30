package com.manager.events_api.domain.coupon;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CouponMapper {
    @Mapping(target = "id", ignore = true)
    Coupon map(CouponRequestDTO couponRequestDTO);

    @Mapping(target = "eventId", source = "event.id")
    CouponResponseDTO toResponseDTO(Coupon coupon);
}
