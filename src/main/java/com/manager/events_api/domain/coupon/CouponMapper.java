package com.manager.events_api.domain.coupon;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CouponMapper {

    Coupon map(CouponRequestDTO couponRequestDTO);

    CouponResponseDTO toResponseDTO(Coupon coupon);
}
