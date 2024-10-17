package io.kamsan.secureinvoices.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePasswordForm {

	@NotEmpty(message = "Current password cannot be empty")
	private String password;
	@NotEmpty(message = " New password cannot be empty")
	private String newPassword;
	@NotEmpty(message = "Confirm Password cannot be empty")
	private String confirmPassword;
}
