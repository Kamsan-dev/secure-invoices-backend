package io.kamsan.secureinvoices.controllers;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.kamsan.secureinvoices.domain.HttpResponse;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.form.LoginForm;
import io.kamsan.secureinvoices.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	
	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	
	@PostMapping("/login")
	public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
		log.info("tentative de connexion avec email {} et mot de passe {}", loginForm.getEmail(), loginForm.getPassword());
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));
		UserDTO userDTO = userService.getUserByEmail(loginForm.getEmail());
		return ResponseEntity.ok().body(
				HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userDTO))
				.message("Login Success")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
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
