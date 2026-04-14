package com.manager.events_api.domain.event;

import com.manager.events_api.infra.exceptions.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class EventService {

    private final EventRepository repository;
    private final EventMapper eventMapper;

    public EventService(EventRepository repository, EventMapper eventMapper) {
        this.repository = repository;
        this.eventMapper = eventMapper;
    }

    public Event createEvent(EventRequestDTO data) {
        if (!data.remote() && data.address() == null) {
            throw new BusinessException("Address is required");
        }
        Event newEvent = eventMapper.map(data);

        if (newEvent.getRemote()) {
            newEvent.setAddress(null);
        }
        return repository.save(newEvent);
    }

    public Page<EventResponseDTO> getUpComingEvents(int page, int size, String title, String city, String uf, OffsetDateTime startDate, OffsetDateTime endDate) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Event> spec = EventSpecifications.getEventsWithFilters(title, city, uf, startDate, endDate);
        return repository.findAll(spec, pageable).map(eventMapper::toResponseDTO);
    }
}
