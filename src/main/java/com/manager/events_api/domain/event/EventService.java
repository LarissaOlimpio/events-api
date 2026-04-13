package com.manager.events_api.domain.event;

import com.manager.events_api.infra.exceptions.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<EventResponseDTO> getUpComingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        OffsetDateTime now = OffsetDateTime.now();
        Page<Event> eventsPage = this.repository.findByDateGreaterThanEqual(now, pageable);
        return eventsPage.map(eventMapper::toResponseDTO);
    }
}
