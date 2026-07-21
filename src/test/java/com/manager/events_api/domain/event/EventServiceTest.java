package com.manager.events_api.domain.event;

import com.manager.events_api.infra.exceptions.BusinessException;
import com.manager.events_api.infra.exceptions.EventFinishedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository repository;
    @InjectMocks
    private EventService eventService;

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateEvent() {
        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().plusDays(5),
                true,
                "http://event.com",
                null
        );
        when(repository.existsByTitleAndDate(data.title(), data.date())).thenReturn(true);
        assertThrows(BusinessException.class, () -> eventService.createEvent(data));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDateInCreateIsInThePast() {
        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().minusDays(5),
                true,
                "http://event.com",
                null
        );
        assertThrows(BusinessException.class, () -> eventService.createEvent(data));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEventIsNotRemoteAndIsWithoutAddressInCreate() {
        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().plusDays(5),
                false,
                "http://event.com",
                null
        );
        assertThrows(BusinessException.class, () -> eventService.createEvent(data));
        verify(repository, never()).save(any());
    }


    @Test
    void shouldThrowExceptionWhenUpdatingFinishedEvent() {
        Event finishedEvent = new Event();
        finishedEvent.setDate(OffsetDateTime.now().minusDays(1));

        UUID eventId = UUID.randomUUID();
        when(repository.findById(eventId)).thenReturn(Optional.of(finishedEvent));

        EventRequestDTO data = new EventRequestDTO(
                "Updated title",
                "Updated description",
                OffsetDateTime.now().plusDays(5),
                true,
                "http://event.com",
                null
        );

        assertThrows(EventFinishedException.class, () -> eventService.updateEvent(eventId, data));
        verify(repository, never()).save(any());

    }

    @Test
    void shouldThrowExceptionWhenNewDateInUpdatingIsInThePast() {
        Event upcomingEvent = new Event();
        upcomingEvent.setDate(OffsetDateTime.now().plusDays(10));

        UUID eventID = UUID.randomUUID();
        when(repository.findById(eventID)).thenReturn(Optional.of(upcomingEvent));

        EventRequestDTO data = new EventRequestDTO(
                "Updated title",
                "Updated description",
                OffsetDateTime.now().minusDays(5),
                true,
                "http://event.com",
                null
        );

        assertThrows(BusinessException.class, () -> eventService.updateEvent(eventID, data));
        verify(repository, never()).save(any());

    }

    @Test
    void shouldThrowExceptionWhenEventIsNotRemoteAndIsWithoutAddressInUpdating() {
        Event event = new Event();
        event.setDate(OffsetDateTime.now().plusDays(20));
        UUID eventId = UUID.randomUUID();

        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        EventRequestDTO data = new EventRequestDTO(
                "Updated title",
                "Updated description",
                OffsetDateTime.now().plusDays(5),
                false,
                "http://event.com",
                null
        );
        assertThrows(BusinessException.class, () -> eventService.updateEvent(eventId, data));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDeleteFinishedEvent() {
        Event event = new Event();
        event.setDate(OffsetDateTime.now().minusDays(1));
        UUID eventId = UUID.randomUUID();

        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(EventFinishedException.class, () -> eventService.deleteEvent(eventId));
        verify(repository, never()).delete(any(Event.class));
    }


}
