package io.kamsan.secureinvoices.event;

import org.springframework.context.ApplicationEvent;

import io.kamsan.secureinvoices.enums.EventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent{
	
	private EventType type;
	private String email;
	public NewUserEvent(String email, EventType type) {
		super(email);
		this.type = type;
		this.email = email;
	}
}
