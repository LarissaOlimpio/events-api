package com.manager.events_api.domain.coupon;

import com.manager.events_api.domain.event.Event;
import com.manager.events_api.domain.event.EventRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CouponService {
    private final EventRepository eventRepository;
    private final CouponRepository couponrepository;
    private final CouponMapper couponMapper;

    public CouponService(CouponRepository couponrepository, EventRepository eventRepository, CouponMapper couponMapper) {
        this.couponrepository = couponrepository;
        this.eventRepository = eventRepository;
        this.couponMapper = couponMapper;
    }

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO data) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        Coupon newCoupon = couponMapper.map(data);
        newCoupon.setEvent(event);
        return couponrepository.save(newCoupon);

    }

}
