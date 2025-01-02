package io.kamsan.secureinvoices.utils;

import static java.time.LocalDateTime.now;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.kamsan.secureinvoices.domain.HttpResponse;
import io.kamsan.secureinvoices.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionUtils {
	
	public static void processError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		
		
		/* exception hidden : TokenExpiredException, InvalidClaimException */
		if (exception instanceof ApiException 
				|| exception instanceof DisabledException
				|| exception instanceof LockedException 
				|| exception instanceof BadCredentialsException) {
			HttpResponse httpResponse = getHttpResponse(request,response, exception.getMessage(), HttpStatus.BAD_REQUEST);
			writeResponse(response, httpResponse);
		} else if (exception instanceof TokenExpiredException){
			HttpResponse httpResponse = getHttpResponse(request,response, exception.getMessage(), HttpStatus.UNAUTHORIZED);
			writeResponse(response, httpResponse);
		} else {
			/* unexpected error */
			HttpResponse httpResponse = getHttpResponse(request,response, "An error occrured. Please try again.", 
					HttpStatus.INTERNAL_SERVER_ERROR);
			writeResponse(response, httpResponse);
		}

		log.error(exception.getMessage());
	}

	private static HttpResponse getHttpResponse(HttpServletRequest request, HttpServletResponse response, String message, HttpStatus httpStatus) {
		String path = (String) request.getRequestURI();
		HttpResponse httpResponse = HttpResponse.builder()
				.timeStamp(now().toString())
				.reason(message)
				.path(path)
				.status(httpStatus)
				.statusCode(httpStatus.value())
				.build();
		
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(httpStatus.value());
		
		return httpResponse;
	}
	
	private static void writeResponse(HttpServletResponse response, HttpResponse httpResponse) {
		// convert java value into json output
		OutputStream out;
		try {
			out = response.getOutputStream();
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(out, httpResponse);
			out.flush();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
