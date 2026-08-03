package com.manager.events_api.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String data);

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
