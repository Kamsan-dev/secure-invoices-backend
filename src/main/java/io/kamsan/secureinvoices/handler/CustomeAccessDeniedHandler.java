package io.kamsan.secureinvoices.handler;

import static java.time.LocalDateTime.now;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.kamsan.secureinvoices.domain.HttpResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomeAccessDeniedHandler implements AccessDeniedHandler{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		HttpResponse httpResponse = HttpResponse.builder()
				.timeStamp(now().toString())
				.reason("You don't have enough permission")
				.status(HttpStatus.FORBIDDEN)
				.statusCode(HttpStatus.FORBIDDEN.value())
				.build();
		
		// convert java value into json output
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.FORBIDDEN.value());
		OutputStream out = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, httpResponse);
		out.flush();
		
	}

}
