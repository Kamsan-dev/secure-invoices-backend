package io.kamsan.secureinvoices.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.kamsan.secureinvoices.event.NewUserEvent;
import io.kamsan.secureinvoices.services.EventService;
import io.kamsan.secureinvoices.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {

	private final EventService eventService;
	private final HttpServletRequest request;
	
	@EventListener
	public void onNewUserEvent(NewUserEvent event) {
		log.info("new user event is fired");
		eventService.addUserEvent(event.getEmail(), event.getType(), RequestUtils.getDevice(request), RequestUtils.getIpAddress(request));
	}
	
}
