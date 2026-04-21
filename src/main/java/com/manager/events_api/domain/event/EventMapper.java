package com.manager.events_api.domain.event;

import com.manager.events_api.domain.address.AddressMapper;
import com.manager.events_api.domain.coupon.CouponMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {AddressMapper.class, CouponMapper.class}
)
public interface EventMapper {

    Event map(EventRequestDTO eventRequestDTO);

    EventResponseDTO toResponseDTO(Event event);

    EventDetailsDTO toDetailsDTO(Event event);

    void updateEventFromDTO(EventRequestDTO dto, @MappingTarget Event event);
}
