package com.manager.events_api.repositories;

import com.manager.events_api.domain.adress.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
