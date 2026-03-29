package com.manager.events_api.domain.event;

import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository repository;
    private final EventMapper eventMapper;

    public EventService(EventRepository repository, EventMapper eventMapper) {
        this.repository = repository;
        this.eventMapper = eventMapper;
    }

    public Event createEvent(EventRequestDTO data) {
        Event newEvent = eventMapper.map(data);
        return repository.save(newEvent);
    }
}
