package com.manager.events_api.domain.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {
    UserResponseDTO toUserResponseDTO(User user);
}
