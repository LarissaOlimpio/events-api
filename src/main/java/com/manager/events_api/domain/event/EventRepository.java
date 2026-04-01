package com.manager.events_api.domain.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    public Page<Event> findByDateGreaterThanEqual(@Param("currentDate") OffsetDateTime currentDate, Pageable pageable);
}
