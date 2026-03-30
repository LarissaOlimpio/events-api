package com.manager.events_api.domain.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    Event map(EventRequestDTO eventRequestDTO);

    @Mapping(target = "id", source = "event.id")
    EventResponseDTO toResponseDTO(Event event);
}
