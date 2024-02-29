package io.kamsan.secureinvoices.controllers;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.kamsan.secureinvoices.domain.CustomeUser;
import io.kamsan.secureinvoices.domain.HttpResponse;
import io.kamsan.secureinvoices.dtomapper.UserDTOMapper;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.form.LoginForm;
import io.kamsan.secureinvoices.provider.TokenProvider;
import io.kamsan.secureinvoices.services.RoleService;
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
	private final RoleService roleService;
	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;

	@PostMapping("/login")
	public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
		log.info("tentative de connexion avec email {} et mot de passe {}", loginForm.getEmail(),
				loginForm.getPassword());
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));
		UserDTO userDTO = userService.getUserByEmail(loginForm.getEmail());
		/* check if user is using MFA to return the right response */
		return (userDTO.isUsingMfa()) ? sendVerificationCode(userDTO) : sendResponse(userDTO);
		
	}

	@PostMapping("/register")
	public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
		UserDTO userDTO = userService.createUser(user);
		return ResponseEntity
				.created(getURI())
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userDTO))
				.message("User created")
				.status(CREATED)
				.statusCode(CREATED
				.value())
				.build());
	}
	
	@GetMapping("/profile")
	public ResponseEntity<HttpResponse> profile(Authentication authentication) {
		UserDTO userDTO = userService.getUserByEmail(authentication.getName());
		return ResponseEntity
				.created(getURI())
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userDTO))
				.message("Profile retrieved")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	@GetMapping("/verify/code/{email}/{code}")
	public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
		UserDTO userDTO = userService.verifyCode(email, code);
		return sendResponse(userDTO);
	}
	
	

	private URI getURI() {
		return URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
	}
	
	private ResponseEntity<HttpResponse> sendResponse(UserDTO userDTO){
		return ResponseEntity.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userDTO, "access-token", 
						tokenProvider.createAccessToken(getCustomUserFromUserDTO(userDTO)),
						"refresh-token", tokenProvider.createRefreshToken(getCustomUserFromUserDTO(userDTO))))
				.message("Login Success")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	
	/* method used by the token provider to generate access-token and refresh-token upon authentication */
	private CustomeUser getCustomUserFromUserDTO(UserDTO userDTO) {
		Role role = roleService.getRoleByUserId(userDTO.getUserId());
		User user = UserDTOMapper.fromUserDTO(userService.getUserByEmail(userDTO.getEmail()));
		return new CustomeUser(user, role);
	}

	private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO userDTO) {
		userService.sendVerificationCode(userDTO);
		return ResponseEntity.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userDTO))
				.message("Verification code sent by SMS")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
}
