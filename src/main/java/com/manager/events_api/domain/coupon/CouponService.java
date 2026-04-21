package com.manager.events_api.domain.coupon;

import com.manager.events_api.domain.event.Event;
import com.manager.events_api.domain.event.EventRepository;
import com.manager.events_api.infra.exceptions.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CouponService {
    private final EventRepository eventRepository;
    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    public CouponService(CouponRepository couponRepository, EventRepository eventRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.eventRepository = eventRepository;
        this.couponMapper = couponMapper;
    }

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO data) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        Coupon newCoupon = couponMapper.map(data);
        newCoupon.setEvent(event);
        return couponRepository.save(newCoupon);

    }

    public Page<CouponResponseDTO> getAllCouponByEvent(UUID eventId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Coupon> CouponPage = this.couponRepository.getAllCouponByEventId(eventId, pageable);
        return CouponPage.map(couponMapper::toResponseDTO);
    }

    @Transactional
    public Coupon updateCoupon(UUID couponId, CouponRequestDTO data) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException("Coupon not found."));

        couponMapper.updateCouponFromDTO(data, coupon);
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(UUID couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException("Coupon not found."));
        couponRepository.delete(coupon);
    }

}
