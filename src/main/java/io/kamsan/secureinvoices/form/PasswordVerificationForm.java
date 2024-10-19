package io.kamsan.secureinvoices.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordVerificationForm {
	
	@NotEmpty(message = "password cannot be empty")
	private String password;

}
