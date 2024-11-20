package io.kamsan.secureinvoices.controllers;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.kamsan.secureinvoices.domain.CustomeUser;
import io.kamsan.secureinvoices.domain.HttpResponse;
import io.kamsan.secureinvoices.dtomapper.UserDTOMapper;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.form.AccountSettingsForm;
import io.kamsan.secureinvoices.form.LoginForm;
import io.kamsan.secureinvoices.form.PasswordVerificationForm;
import io.kamsan.secureinvoices.form.UpdateAuthenticationForm;
import io.kamsan.secureinvoices.form.UpdatePasswordForm;
import io.kamsan.secureinvoices.form.UpdateUserForm;
import io.kamsan.secureinvoices.form.UpdateUserRoleForm;
import io.kamsan.secureinvoices.provider.TokenProvider;
import io.kamsan.secureinvoices.services.RoleService;
import io.kamsan.secureinvoices.services.UserService;
import io.swagger.v3.oas.models.media.MediaType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";
	@Value("${user.profile.image.path}")
    private String imagePath;

	@PostMapping("/login")
	public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
		Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
		UserDTO userAuthenticated = getAuthenticatedUser(authentication);
		/* check if user is using MFA to return the right response */
		return (userAuthenticated.isUsingMfa()) 
				? sendVerificationCode(userAuthenticated) : sendResponse(userAuthenticated);
		
	}
	
	private UserDTO getAuthenticatedUser (Authentication authentication) {
		return ((CustomeUser) authentication.getPrincipal()).getUser();
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
		UserDTO user = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", user, "roles", roleService.getRoles()))
				.message("Profile retrieved")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	@PatchMapping("/update")
	public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateUserForm user) {
		UserDTO updatedUser = userService.updateUserDetails(user);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", updatedUser, "roles", roleService.getRoles()))
				.message("User informations updated")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
//	USER PASSWORD MANIPULATION
	
	@PostMapping("/update/password/verification")
	public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid PasswordVerificationForm form) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		log.info("password received: {}", form.getPassword());
		userService.verifyPassword(userDTO.getUserId(), form.getPassword());
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.message("User password confirmed")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}

	@PatchMapping("/update/password")
	public ResponseEntity<HttpResponse> updatePassword(@RequestBody @Valid UpdatePasswordForm form) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		userService.updatePassword(userDTO.getUserId(), form.getPassword(), form.getNewPassword(), form.getConfirmPassword());
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.message("Password updated successfully")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	@PatchMapping("/update/account-settings")
	@PreAuthorize("hasAuthority('UPDATE:USER')")
	public ResponseEntity<HttpResponse> updateAccountSettings(@RequestBody @Valid AccountSettingsForm form) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		userService.updateAccountSettings(userDTO.getUserId(), form.getEnabled(), form.getNotLocked());
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserById(userDTO.getUserId()), "roles", roleService.getRoles()))
				.message("Account settings updated successfully")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	@PatchMapping("/update/image")
	public ResponseEntity<HttpResponse> updateProfileImage(@RequestParam("image") MultipartFile image) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		userService.updateImage(userDTO, image);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.data(of("user", userService.getUserById(userDTO.getUserId()), "roles", roleService.getRoles()))
				.timeStamp(now().toString())
				.message("Profile image updated successfully")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	@GetMapping(value="/image/{fileName}", produces = IMAGE_PNG_VALUE)
	public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws IOException {
		return Files.readAllBytes(Paths.get(imagePath, fileName));
	}
	
	
	@PatchMapping("/update/authentication-settings")
	public ResponseEntity<HttpResponse> updateAuthenticationSettings(@RequestBody @Valid UpdateAuthenticationForm form) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		userService.updateAuthenticationSettings(userDTO.getUserId(), form.getUsingMfa());
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserById(userDTO.getUserId()), "roles", roleService.getRoles()))
				.message("Account settings updated successfully")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	@PatchMapping("/update/role")
	@PreAuthorize("hasAuthority('UPDATE:USER')")
	public ResponseEntity<HttpResponse> updateRole(@RequestBody UpdateUserRoleForm roleForm) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
	    // Log the authorities of the authenticated user
	    List<String> authorities = authentication.getAuthorities().stream()
	        .map(GrantedAuthority::getAuthority)
	        .collect(Collectors.toList());
	    
	    // Log the authorities (you can log it to the console, or use a logger)
	    log.info("Authenticated user authorities: {}", String.join(", ", authorities));
		UserDTO userDTO = getAuthenticatedUser(authentication);
		userService.updateUserRole(userDTO.getUserId(), roleForm.getRoleName());
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.data(of("user", userService.getUserById(userDTO.getUserId()), "roles", roleService.getRoles()))
				.timeStamp(now().toString())
				.message("Role updated successfully")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	
	
	@GetMapping("/verify/account/{key}")
	public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {
		UserDTO userDTO = userService.verifyAccountKey(key);
		String message = (userDTO.isEnabled()) ? "Your account is already verified" : "Your account has been verified";
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.message(message)
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	// to reset password when user is not logged in.
	
	@GetMapping("/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) {
		userService.resetPassword(email);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.message("We’ve sent you an email. Please check your email to reset your password.")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	@GetMapping("/verify/password/{key}")
	public ResponseEntity<HttpResponse> verifyPasswordKey(@PathVariable("key") String key) {
		UserDTO userDTO = userService.verifyPasswordKey(key);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userDTO))
				.message("Please enter a new password")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	@PostMapping("/resetpassword/{key}/{password}/{confirmPassword}")
	public ResponseEntity<HttpResponse> resetPasswordWithKey(@PathVariable("key") String key, 
			@PathVariable("password") String password, @PathVariable("confirmPassword") String confirmPassword) {
		userService.renewPassword(key, password, confirmPassword);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.message("Password has been reset successfully.")
				.status(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}
	
	// END - to reset password when user is not logged in.

	/* handle white label error */
//	@RequestMapping("/error")
//	public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
//		return ResponseEntity.badRequest().body(
//				HttpResponse.builder()
//					.timeStamp(now().toString())
//					.reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
//					.path(request.getRequestURI())
//					.status(HttpStatus.NOT_FOUND)
//					.statusCode(HttpStatus.NOT_FOUND.value())
//					.build()
//		);
//	}
	
	@RequestMapping("/error")
	public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
		return new ResponseEntity<>(HttpResponse.builder()
					.timeStamp(now().toString())
					.reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
					.path(request.getRequestURI())
					.status(HttpStatus.NOT_FOUND)
					.statusCode(HttpStatus.NOT_FOUND.value())
					.build(),(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping("/verify/code/{email}/{code}")
	public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
		UserDTO userDTO = userService.verifyCode(email, code);
		return sendResponse(userDTO);
	}
	
	
	@GetMapping("/refresh/token")
	public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
		log.info("inside refreshToken");
		if (isHeaderAndTokenValid(request)) {
			String refreshtoken = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
			UserDTO userDTO = userService.getUserById(tokenProvider.getSubject(refreshtoken, request));	
			return ResponseEntity.ok().body(HttpResponse.builder()
					.timeStamp(now().toString())
					.data(of("user", userDTO, "access_token", 
							tokenProvider.createAccessToken(getCustomUserFromUserDTO(userDTO)),
							"refresh_token", refreshtoken))
					.message("Token refreshed")
					.status(HttpStatus.OK)
					.statusCode(HttpStatus.OK.value())
					.build());
		}
		else {
			return ResponseEntity.badRequest().body(
					HttpResponse.builder()
						.timeStamp(now().toString())
						.reason("Refresh token missing or invalid")
						.developerMessage("Refresh token missing or invalid")
						.status(HttpStatus.NOT_FOUND)
						.statusCode(HttpStatus.NOT_FOUND.value())
						.build()
			);
		}

	}
	
//	private boolean isHeaderAndTokenValid(HttpServletRequest request) {
//		String token = request.getHeader("Authorization").substring(TOKEN_PREFIX.length());
//		String subject = tokenProvider.getSubject(token, request);
//		
//		return (request.getHeader(AUTHORIZATION) != null &&
//				request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) &&
//				tokenProvider.isTokenValid(subject, token));
//	}
	
	private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return  request.getHeader(AUTHORIZATION) != null
                &&  request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(
                        tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request),
                        request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length())
                            );
    }

	private URI getURI() {
		return URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
	}
	
	private ResponseEntity<HttpResponse> sendResponse(UserDTO userDTO){
		return ResponseEntity.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userDTO, "access_token", 
						tokenProvider.createAccessToken(getCustomUserFromUserDTO(userDTO)),
						"refresh_token", tokenProvider.createRefreshToken(getCustomUserFromUserDTO(userDTO))))
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
	
	/* authentication allows access to the authenticated user */
	private Authentication authenticate(String email, String password) {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, password));
			return authentication;
		} catch (Exception exception) {
			//ExceptionUtils.processError(request, response, exception);
			throw new ApiException("Incorrect email or password.");
		}
	}
}
