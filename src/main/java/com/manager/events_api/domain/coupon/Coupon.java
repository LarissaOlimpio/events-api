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
    private OffsetDateTime validFrom;
    private OffsetDateTime validUntil;
    private Integer discount;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OffsetDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(OffsetDateTime valid) {
        this.validFrom = valid;
    }

    public OffsetDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(OffsetDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
