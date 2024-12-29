package io.kamsan.secureinvoices.services.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.kamsan.secureinvoices.enums.VerificationType;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.services.EmailService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String fromEmail;

	@Override
	public void sendVerificationEmail(String firstName, String email, String verificationUrl,
			VerificationType verificationType) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(email);
			message.setSubject(String.format("SecureInvoices - %s Verification Email",
					StringUtils.capitalize(verificationType.getType())));
			message.setText(getEmailMessage(firstName, verificationUrl, verificationType));
			mailSender.send(message);
			log.info("Email sent to {}", firstName);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

	}

	private String getEmailMessage(String firstName, String verificationUrl, VerificationType verificationType) {
		switch (verificationType) {
		case PASSWORD -> {
			return "Hello " + firstName + ","
					+ "\n\nReset password request. Please click the link below to reset your password. \n\n"
					+ verificationUrl + "\n\nThe Support Team";
		}
		case ACCOUNT -> {
			return "Hello " + firstName + ","
					+ "\n\nYour account has been created. Please click the link below to activate your account. \n\n"
					+ verificationUrl + "\n\nThe Support Team";
		}
		default -> throw new ApiException("Unable to send email. Email type is unknown");
		}
	}

}
