package io.kamsan.secureinvoices.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountSettingsForm {
	
	@NotNull(message = "Enabled cannot be null or empty")
	private Boolean enabled;
	@NotNull(message = "Not locked cannot be null or empty")
	private Boolean notLocked;
	

}
