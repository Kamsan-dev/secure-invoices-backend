package io.kamsan.secureinvoices.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

	@Email(message = "Invalid email. Please enter a valid email address")
	private String email;
	@NotEmpty(message = "Password cannot be empty")
	private String password;

}
