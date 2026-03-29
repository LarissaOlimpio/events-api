package com.manager.events_api.domain.coupon;

import com.manager.events_api.domain.event.Event;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private OffsetDateTime valid;
    private Integer discount;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
