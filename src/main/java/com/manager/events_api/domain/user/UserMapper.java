package com.manager.events_api.domain.user;

import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {
    UserResponseDTO toUserResponseDTO(User user);

    void updateUserFromDTO(@Valid UserUpdateRequestDTO data, @MappingTarget User user);
}
