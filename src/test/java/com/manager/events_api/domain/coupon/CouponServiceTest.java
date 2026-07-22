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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponMapper couponMapper;

    @InjectMocks
    private CouponService couponService;

    @Test
    void shouldAddCouponSuccessfullyWhenDatesAreValid() {
        Event event = new Event();
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        CouponRequestDTO couponRequestDTO = new CouponRequestDTO(
                "SUMMER10",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                20
        );

        Coupon mappedCoupon = new Coupon();
        when(couponMapper.map(couponRequestDTO)).thenReturn(mappedCoupon);
        when(couponRepository.save(any(Coupon.class))).thenReturn(mappedCoupon);
        Coupon result = couponService.addCouponToEvent(eventId, couponRequestDTO);

        assertNotNull(result);
        verify(couponMapper, times(1)).map(couponRequestDTO);
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void shouldThrowExceptionWhenValidFromWasAfterValidUntilInAddCouponToEvent() {
        Event event = new Event();
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

    @Test
    void shouldThrowExceptionWhenValidUntilWasBeforeCurrentDateInAddCouponToEvent() {
        Event event = new Event();
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        CouponRequestDTO coupon = new CouponRequestDTO(
                "SUMMER10",
                OffsetDateTime.now(),
                OffsetDateTime.now().minusDays(3),
                20
        );

        assertThrows(BusinessException.class, () -> couponService.addCouponToEvent(eventId, coupon));
        verify(couponRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCouponSuccessfullyWhenDatesAreValid() {
        Coupon existingCoupon = new Coupon();
        UUID couponId = UUID.randomUUID();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(existingCoupon));

        CouponRequestDTO couponRequestDTO = new CouponRequestDTO(
                "SUMMER10",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                20
        );


        when(couponRepository.save(any(Coupon.class))).thenReturn(existingCoupon);
        couponService.updateCoupon(couponId, couponRequestDTO);

        verify(couponMapper, times(1)).updateCouponFromDTO(couponRequestDTO, existingCoupon);
        verify(couponRepository, times(1)).save(existingCoupon);

    }

    @Test
    void shouldThrowExceptionWhenValidFromWasAfterValidUntilInUpdateCoupon() {

        Coupon coupon = new Coupon();
        UUID couponId = UUID.randomUUID();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        CouponRequestDTO couponUpdate = new CouponRequestDTO(
                "SUMMER10",
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now(),
                20
        );

        assertThrows(BusinessException.class, () -> couponService.updateCoupon(couponId, couponUpdate));
        verify(couponRepository, never()).save(any());

    }

    @Test
    void shouldThrowExceptionWhenValidUntilWasBeforeCurrentDateInUpdateCoupon() {
        Coupon coupon = new Coupon();
        UUID couponId = UUID.randomUUID();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        CouponRequestDTO couponUpdate = new CouponRequestDTO(
                "SUMMER10",
                OffsetDateTime.now(),
                OffsetDateTime.now().minusDays(3),
                20
        );

        assertThrows(BusinessException.class, () -> couponService.updateCoupon(couponId, couponUpdate));
        verify(couponRepository, never()).save(any());
    }
}
