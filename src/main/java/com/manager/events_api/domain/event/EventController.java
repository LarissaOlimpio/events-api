package com.manager.events_api.domain.event;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
    public ResponseEntity<EventResponseDTO> create(@RequestBody EventRequestDTO data, UriComponentsBuilder uriBuilder) {
        Event newEvent = this.eventService.createEvent(data);
        EventResponseDTO response = eventMapper.toResponseDTO(newEvent);
        var uri = uriBuilder.path("/api/event/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> getEvents(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<EventResponseDTO> allEvents = this.eventService.getAllEvents(page, size);
        return ResponseEntity.ok(allEvents);
    }
}
