package com.manager.events_api.domain.coupon;

import com.manager.events_api.domain.event.Event;
import com.manager.events_api.domain.event.EventRepository;
import com.manager.events_api.infra.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void shouldThrowExceptionWhenValidFromWasAfterValidUntil() {
        Event event = new Event();
        event.setDate(OffsetDateTime.now().plusDays(3));

        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        CouponRequestDTO coupon = new CouponRequestDTO(
                "SUMMER10",
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now(),
                20
        );

        assertThrows(BusinessException.class, () -> couponService.addCouponToEvent(eventId, coupon));
        verify(couponRepository, never()).save(any());

    }

}
