package com.manager.events_api.domain.coupon;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Page<Coupon> getAllCouponByEventId(UUID eventId, Pageable pageable);
}
