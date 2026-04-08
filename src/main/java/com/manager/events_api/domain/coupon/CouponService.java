package com.manager.events_api.domain.coupon;

import com.manager.events_api.domain.event.Event;
import com.manager.events_api.domain.event.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
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

    public Page<CouponResponseDTO> getAllCouponByEvent(UUID eventId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Coupon> CouponPage = this.couponrepository.getAllCouponByEventId(eventId, pageable);
        return CouponPage.map(couponMapper::toResponseDTO);
    }

}
