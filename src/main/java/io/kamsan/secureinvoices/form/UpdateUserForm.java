package io.kamsan.secureinvoices.form;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserForm {
	
	@NotNull(message = "id cannot be null or empty")
	private Long userId;
	@NotEmpty(message = "First name cannot be empty")
	private String firstName;
	@NotEmpty(message = "Last name cannot be empty")
	private String lastName;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email. Please enter a valid email address")
	private String email;
	@Pattern(regexp = "^(\\+33|0)[67](\\d{8})$", message = "Invalid phone number. Please enter a valid phone number")
	private String phone;
	private String address;
	private String title;
	private String bio;

}
