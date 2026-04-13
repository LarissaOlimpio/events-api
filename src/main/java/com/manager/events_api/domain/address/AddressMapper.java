package com.manager.events_api.domain.address;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {

    Address map(AddressRequestDTO addressRequestDTO);

    AddressResponseDTO toResponseDTO(Address address);
}
