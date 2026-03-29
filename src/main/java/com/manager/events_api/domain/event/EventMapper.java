package com.manager.events_api.domain.event;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    Event map(EventRequestDTO eventRequestDTO);
}
