package io.kamsan.secureinvoices.services;

import java.util.Collection;

import io.kamsan.secureinvoices.entities.UserEvent;
import io.kamsan.secureinvoices.enums.EventType;

public interface EventService {
	Collection<UserEvent> getEventsByUserId(Long userId);
	void addUserEvent(String email, EventType eventType, String device, String ipAddress);
	void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
}
