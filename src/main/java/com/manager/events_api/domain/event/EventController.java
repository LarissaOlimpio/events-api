package com.manager.events_api.domain.event;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("api/event")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody EventRequestDTO data, UriComponentsBuilder uriBuilder) {
        Event newEvent = this.eventService.createEvent(data);
        var uri = uriBuilder.path("/api/event/{id}").buildAndExpand(newEvent.getId()).toUri();
        return ResponseEntity.created(uri).body(newEvent);
    }
}
