package io.kamsan.secureinvoices.services;

import io.kamsan.secureinvoices.enums.VerificationType;

public interface EmailService {

	void sendVerificationEmail(String firstName, String email, String verificationUrl,
			VerificationType verificationType);

}
