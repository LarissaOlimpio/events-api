package com.manager.events_api.domain.event;

import com.manager.events_api.domain.address.AddressMapper;
import com.manager.events_api.infra.exceptions.BusinessException;
import com.manager.events_api.infra.exceptions.EventFinishedException;
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
    private final AddressMapper addressMapper;

    public EventService(EventRepository repository, EventMapper eventMapper, AddressMapper addressMapper) {
        this.repository = repository;
        this.eventMapper = eventMapper;
        this.addressMapper = addressMapper;
    }

    public Event createEvent(EventRequestDTO data) {
        ensureEventDoesNotAlreadyExist(data);
        validEventDate(data.date());
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
        ensureEventIsEditable(event);
        this.repository.delete(event);
    }

    @Transactional
    public Event updateEvent(UUID eventId, EventRequestDTO data) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found."));
        ensureEventIsEditable(event);
        validEventDate(data.date());
        handleAddressUpdate(event, data);
        eventMapper.updateEventFromDTO(data, event);

        return repository.save(event);
    }

    private void handleAddressUpdate(Event event, EventRequestDTO data) {
        if (Boolean.TRUE.equals(data.remote())) {
            event.setAddress(null);
            return;
        }
        if (data.address() == null) {
            throw new BusinessException("Address is required");
        }
        if (event.getAddress() == null) {
            event.setAddress(addressMapper.map(data.address()));
        } else {
            addressMapper.update(data.address(), event.getAddress());
        }

    }

    private void ensureEventIsEditable(Event event) {
        if (event.getStatus() == EventStatus.FINISHED) {
            throw new EventFinishedException("Finished events cannot be edited or deleted");
        }
    }

    private void validEventDate(OffsetDateTime date) {
        if (date.isBefore(OffsetDateTime.now())) {
            throw new BusinessException("Event date must be in the future");
        }
    }

    private void ensureEventDoesNotAlreadyExist(EventRequestDTO data) {
        boolean exists = repository.existsByTitleAndDate(data.title(), data.date());
        if (exists) {
            throw new BusinessException("An event with this title and date already exists");
        }
    }
}
