package com.manager.events_api.domain.event;

import com.manager.events_api.domain.address.Address;
import com.manager.events_api.infra.exceptions.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

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

    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = this.repository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found with the provided ID"));
        return eventMapper.toDetailsDTO(event);

    }

    public void deleteEvent(UUID eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found."));
        this.repository.delete(event);
    }

    @Transactional
    public Event updateEvent(UUID eventId, EventRequestDTO data) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found."));
        eventMapper.updateEventFromDTO(data, event);
        handleAddressUpdate(event, data);

        return repository.save(event);
    }

    private void handleAddressUpdate(Event event, EventRequestDTO data) {
        if (Boolean.TRUE.equals(data.remote())) {
            event.setAddress(null);
            return;
        }
        if (event.getAddress() == null) {
            event.setAddress(new Address());
        }

        Address address = event.getAddress();
        address.setCity(data.city());
        address.setUf(data.uf());
        address.setEvent(event);
    }
}
