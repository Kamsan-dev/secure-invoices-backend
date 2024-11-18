package io.kamsan.secureinvoices.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAuthenticationForm {

	@NotNull(message = "usingMfa cannot be null or empty")
	private Boolean usingMfa;
}
