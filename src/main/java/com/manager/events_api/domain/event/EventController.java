package com.manager.events_api.domain.event;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;


@RestController
@RequestMapping("api/event")
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> create(@RequestBody @Valid EventRequestDTO data, UriComponentsBuilder uriBuilder) {
        Event newEvent = this.eventService.createEvent(data);
        EventResponseDTO response = eventMapper.toResponseDTO(newEvent);
        var uri = uriBuilder.path("/api/event/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> getEvents(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(required = false) String title,
                                                            @RequestParam(required = false) String city,
                                                            @RequestParam(required = false) String uf,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {
        Page<EventResponseDTO> allEvents = this.eventService.getUpComingEvents(page, size, title, city, uf, startDate, endDate);
        return ResponseEntity.ok(allEvents);
    }

    @GetMapping("/{eventID}")
    public ResponseEntity<EventDetailsDTO> getEventsDetails(@PathVariable UUID eventID) {
        EventDetailsDTO eventDetailsDTO = eventService.getEventDetails(eventID);
        return ResponseEntity.ok(eventDetailsDTO);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> update(@PathVariable UUID eventId, @RequestBody @Valid EventRequestDTO data) {
        Event updatedEvent = this.eventService.updateEvent(eventId, data);
        return ResponseEntity.ok(eventMapper.toResponseDTO(updatedEvent));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId) {
        this.eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
