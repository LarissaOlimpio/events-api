package com.manager.events_api.domain.coupon;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {
    private final CouponService couponService;
    private final CouponMapper couponMapper;

    public CouponController(CouponService couponService, CouponMapper couponMapper) {
        this.couponService = couponService;
        this.couponMapper = couponMapper;
    }

    @PostMapping("event/{eventId}")
    public ResponseEntity<CouponResponseDTO> create(@PathVariable UUID eventId, @RequestBody CouponRequestDTO data) {
        Coupon newCoupon = this.couponService.addCouponToEvent(eventId, data);
        CouponResponseDTO response = couponMapper.toResponseDTO(newCoupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("event/{eventId}")
    public ResponseEntity<Page<CouponResponseDTO>> getCoupons(@PathVariable UUID eventId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<CouponResponseDTO> allCoupons = this.couponService.getAllCouponByEvent(eventId, page, size);
        return ResponseEntity.ok(allCoupons);
    }
}
