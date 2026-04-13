package com.manager.events_api.domain.event;

import com.manager.events_api.domain.address.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {AddressMapper.class}
)
public interface EventMapper {

    Event map(EventRequestDTO eventRequestDTO);

    EventResponseDTO toResponseDTO(Event event);
}
