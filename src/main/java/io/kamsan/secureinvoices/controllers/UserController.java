package io.kamsan.secureinvoices.controllers;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;

import java.net.URI;

import static org.springframework.http.HttpStatus.CREATED;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.HttpResponse;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
		UserDTO userDTO = userService.createUser(user);
		return ResponseEntity.created(getURI()).body(
			HttpResponse.builder()
			.timeStamp(now().toString())
			.data(of("user", userDTO))
			.message("User created")
			.status(CREATED)
			.statusCode(CREATED.value())
			.build());	
	}
	
	private URI getURI() {
		return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/user/get/<userId>").toUriString());
	}
}
