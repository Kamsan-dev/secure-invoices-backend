package io.kamsan.secureinvoices.services.implementation;

import java.util.Collection;

import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.entities.UserEvent;
import io.kamsan.secureinvoices.enums.EventType;
import io.kamsan.secureinvoices.repositories.EventRepository;
import io.kamsan.secureinvoices.services.EventService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
	private final EventRepository eventRepository;

	@Override
	public Collection<UserEvent> getEventsByUserId(Long userId) {
		return eventRepository.getEventsByUserId(userId);
	}

	@Override
	public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
		eventRepository.addUserEvent(email, eventType, device, ipAddress);
		
	}

	@Override
	public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {
		eventRepository.addUserEvent(userId, eventType, device, ipAddress);
		
	}

}
