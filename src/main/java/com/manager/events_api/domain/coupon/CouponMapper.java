package com.manager.events_api.domain.coupon;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CouponMapper {

    Coupon map(CouponRequestDTO couponRequestDTO);

    CouponResponseDTO toResponseDTO(Coupon coupon);

    void updateCouponFromDTO(CouponRequestDTO dto, @MappingTarget Coupon coupon);
}
