package io.kamsan.secureinvoices.repositories.implementation;

import static io.kamsan.secureinvoices.query.UserEventQuery.*;

import java.util.Collection;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import io.kamsan.secureinvoices.entities.UserEvent;
import io.kamsan.secureinvoices.enums.EventType;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.repositories.EventRepository;
import io.kamsan.secureinvoices.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {

	private final NamedParameterJdbcTemplate jdbc;

	@Override
	public Collection<UserEvent> getEventsByUserId(Long userId) {
		try {
			return jdbc.query(SELECT_EVENTS_BY_USER_ID, Map.of("userId", userId), new UserEventRowMapper());
		} catch (DataAccessException e) {
			log.error("Failed to retrieve events for user ID: {}", userId, e);
			throw new ApiException("An error occured when retrieving user events.");
		}
	}

	@Override
	public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
		try {
			log.info("STARTING ADDING USER EVENT : device : {}, ipAddress : {}, type : {}", device, ipAddress, eventType.toString());
			jdbc.update(INSERT_USER_EVENT_BY_EMAIL_QUERY,
					Map.of("email", email, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
		} catch (Exception exception) {
			log.error(exception.getMessage());
			throw new ApiException("An error occured when registering a user event, please try again.");
		}

	}

	@Override
	public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {
		try {
			log.info("STARTING ADDING USER EVENT : device : {}, ipAddress : {}, type : {}", device, ipAddress, eventType.toString());
			jdbc.update(INSERT_USER_EVENT_BY_USER_ID_QUERY,
					Map.of("userId", userId, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
		} catch (Exception exception) {
			log.error(exception.getMessage());
			throw new ApiException("An error occured when registering a user event, please try again.");
		}

	}

}
