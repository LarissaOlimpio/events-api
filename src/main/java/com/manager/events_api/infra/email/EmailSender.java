package com.manager.events_api.infra.email;

public interface EmailSender {
    void send(String to, String subject, String body);
}
