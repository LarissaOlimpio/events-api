package com.manager.events_api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class EventFinishedException extends BusinessException {
    public EventFinishedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
