package com.manager.events_api.domain.event;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> getEventsWithFilters(String title, String city, String uf, OffsetDateTime startDate, OffsetDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            OffsetDateTime now = OffsetDateTime.now();

            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), now));

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (city != null && !city.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.join("address").get("city")), "%" + city.toLowerCase() + "%"));
            }

            if (uf != null && !uf.isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.join("address").get("uf")), uf.toLowerCase()));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
