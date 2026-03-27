package com.manager.events_api.domain.coupon;

import com.manager.events_api.domain.event.Event;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private Date valid;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
