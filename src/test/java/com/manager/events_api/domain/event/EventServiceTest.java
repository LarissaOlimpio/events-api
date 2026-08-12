package com.manager.events_api.domain.event;

import com.manager.events_api.domain.address.Address;
import com.manager.events_api.domain.address.AddressMapper;
import com.manager.events_api.domain.address.AddressRequestDTO;
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

    @Mock
    private EventMapper eventMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldCreateEventSuccessfullyWhenEventIsRemoteAndAddressIsNull() {
        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().plusDays(5),
                true,
                "http://event.com",
                null
        );
        Event event = new Event();
        event.setRemote(true);
        when(eventMapper.map(data)).thenReturn(event);
        when(repository.save(any(Event.class))).thenReturn(event);
        Event result = eventService.createEvent(data);

        assertNotNull(result);
        assertTrue(result.getRemote());
        assertNull(result.getAddress());
        verify(eventMapper).map(data);
        verify(repository).save(any(Event.class));

    }

    @Test
    void shouldCreateEventSuccessfullyWhenEventIsRemoteAndRemoveAddress() {
        Address address = new Address();
        address.setCity("Sao Paulo");
        address.setUf("SP");

        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().plusDays(5),
                true,
                "http://event.com",
                null
        );

        Event event = new Event();
        event.setRemote(true);
        event.setAddress(address);

        when(eventMapper.map(data)).thenReturn(event);
        when(repository.save(event)).thenReturn(event);

        Event result = eventService.createEvent(data);

        assertNotNull(result);
        assertTrue(result.getRemote());
        assertNull(result.getAddress());

        verify(eventMapper).map(data);
        verify(repository).save(event);
    }

    @Test
    void shouldCreateEventSuccessfullyWhenEventIsNotRemoteAndAddressIsFull() {
        AddressRequestDTO addressDTO = new AddressRequestDTO(
                "Sao Paulo",
                "SP"
        );
        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().plusDays(5),
                false,
                "http://event.com",
                addressDTO
        );
        Address address = new Address();
        address.setCity("Sao Paulo");
        address.setUf("SP");

        Event event = new Event();
        event.setRemote(false);
        event.setAddress(address);

        when(eventMapper.map(data)).thenReturn(event);
        when(repository.save(any(Event.class))).thenReturn(event);
        Event result = eventService.createEvent(data);

        assertNotNull(result);
        assertFalse(result.getRemote());
        assertNotNull(result.getAddress());
        verify(eventMapper).map(data);
        verify(repository).save(any(Event.class));

    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicatedEvent() {
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
        verify(eventMapper, never()).map(any());
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
        verify(eventMapper, never()).map(any());
        verify(repository, never()).save(any());

    }

    @Test
    void shouldUpdateEventSuccessfullyWhenEventIsRemoteAndAddressIsNull() {
        UUID eventId = UUID.randomUUID();

        Address address = new Address();
        address.setCity("Sao Paulo");
        address.setUf("SP");

        Event event = new Event();
        event.setDate(OffsetDateTime.now().plusDays(20));
        event.setRemote(false);
        event.setAddress(address);

        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().plusDays(5),
                true,
                "http://event.com",
                null
        );

        when(repository.findById(eventId)).thenReturn(Optional.of(event));
        when(repository.save(event)).thenReturn(event);

        event.setRemote(true);
        Event result = eventService.updateEvent(eventId, data);

        assertNotNull(result);
        assertNull(result.getAddress());
        verify(eventMapper).updateEventFromDTO(data, event);
        verify(repository).save(any(Event.class));

    }

    @Test
    void shouldUpdateEventSuccessfullyWhenEventIsNotRemoteAndAlreadyHasAddress() {
        UUID eventId = UUID.randomUUID();

        Address address = new Address();
        address.setCity("Rio de Janeiro");
        address.setUf("RJ");

        Event event = new Event();
        event.setDate(OffsetDateTime.now().plusDays(20));
        event.setRemote(false);
        event.setAddress(address);

        AddressRequestDTO addressDTO = new AddressRequestDTO(
                "Sao Paulo",
                "SP"
        );

        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().plusDays(5),
                false,
                "http://event.com",
                addressDTO
        );

        when(repository.findById(eventId)).thenReturn(Optional.of(event));
        when(repository.save(event)).thenReturn(event);

        Event result = eventService.updateEvent(eventId, data);

        assertNotNull(result);
        assertNotNull(result.getAddress());

        verify(repository).findById(eventId);
        verify(addressMapper).update(addressDTO, address);
        verify(eventMapper).updateEventFromDTO(data, event);
        verify(repository).save(event);
    }

    @Test
    void shouldUpdateEventSuccessfullyWhenEventIsNotRemoteAndHasNoPreviousAddress() {
        UUID eventId = UUID.randomUUID();

        Event event = new Event();
        event.setDate(OffsetDateTime.now().plusDays(20));
        event.setRemote(true);
        event.setAddress(null);

        AddressRequestDTO addressDTO = new AddressRequestDTO(
                "Sao Paulo",
                "SP"
        );

        EventRequestDTO data = new EventRequestDTO(
                "Workshop Java",
                "Description",
                OffsetDateTime.now().plusDays(5),
                false,
                "http://event.com",
                addressDTO
        );

        Address mappedAddress = new Address();
        mappedAddress.setCity("Sao Paulo");
        mappedAddress.setUf("SP");

        when(repository.findById(eventId)).thenReturn(Optional.of(event));
        when(addressMapper.map(addressDTO)).thenReturn(mappedAddress);
        when(repository.save(event)).thenReturn(event);

        Event result = eventService.updateEvent(eventId, data);

        assertNotNull(result);
        assertNotNull(result.getAddress());
        assertSame(mappedAddress, result.getAddress());

        verify(addressMapper).map(addressDTO);
        verify(eventMapper).updateEventFromDTO(data, event);
        verify(repository).save(event);
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
        verify(eventMapper, never()).updateEventFromDTO(data, finishedEvent);
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
        verify(eventMapper, never()).updateEventFromDTO(data, event);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteSuccessfullyWhenEventWasNotFinished() {
        Event event = new Event();
        event.setDate(OffsetDateTime.now().plusDays(1));
        UUID eventId = UUID.randomUUID();

        when(repository.findById(eventId)).thenReturn(Optional.of(event));
        eventService.deleteEvent(eventId);

        verify(repository).delete(any(Event.class));
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
