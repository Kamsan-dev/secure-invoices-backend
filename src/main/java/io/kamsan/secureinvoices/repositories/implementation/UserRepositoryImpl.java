package io.kamsan.secureinvoices.repositories.implementation;

import static io.kamsan.secureinvoices.enums.RoleType.ROLE_USER;
import static io.kamsan.secureinvoices.enums.VerificationType.ACCOUNT;
import static io.kamsan.secureinvoices.enums.VerificationType.PASSWORD;
import static io.kamsan.secureinvoices.query.UserQuery.COUNT_USER_EMAIL_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.DELETE_PASSWORD_VERIFICATION_BY_USERID_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.DELETE_VERIFICATION_BY_URL_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.DELETE_VERIFICATION_CODE_BY_USERID_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.INSERT_ACCOUNT_VERIFICATION_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.INSERT_URL_PASSWORD_VERIFICATION_CODE_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.INSERT_USER_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.INSERT_VERIFICATION_CODE_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.SELECT_CODE_EXPIRATION_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.SELECT_EXPIRATION_BY_URL_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.SELECT_USER_BY_ACCOUNT_URL_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.SELECT_USER_BY_CODE_USER_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.SELECT_USER_BY_EMAIL_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.SELECT_USER_BY_PASSWORD_URL_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.SELECT_USER_BY_USERID_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.UPDATE_USER_AUTHENTICATION_SETTINGS_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.UPDATE_USER_DETAILS_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.UPDATE_USER_ENABLED_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.UPDATE_USER_IMAGE_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.UPDATE_USER_PASSWORD_BY_ID_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.UPDATE_USER_PASSWORD_BY_URL_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.UPDATE_USER_SETTINGS_QUERY;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addDays;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.kamsan.secureinvoices.domain.CustomeUser;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.enums.VerificationType;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.form.UpdateUserForm;
import io.kamsan.secureinvoices.repositories.RoleRepository;
import io.kamsan.secureinvoices.repositories.UserRepository;
import io.kamsan.secureinvoices.rowmapper.UserRowMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

	private final NamedParameterJdbcTemplate jdbc;
	private final RoleRepository<Role> roleRepository;
	private final BCryptPasswordEncoder encoder;
	/* Default SQL format date */
	private final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	@Value("${user.profile.image.path}")
    private String imagePath;

	@Override
	public User create(User user) {
		// Check the email is unique
		if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0)
			throw new ApiException("Email already in use. Please use a different email and try again");
		// Save new user
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			/* creating parameters for SQL Query */
			SqlParameterSource parameters = getSqlParametersSource(user);
			jdbc.update(INSERT_USER_QUERY, parameters, holder);
			user.setUserId(Objects.requireNonNull(holder.getKey().longValue()));
			// Add role to the user
			roleRepository.addRoleToUser(user.getUserId(), ROLE_USER.toString());
			// Send verification url
			String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
			// Save URL in verification table
			jdbc.update(INSERT_ACCOUNT_VERIFICATION_QUERY, Map.of("userId", user.getUserId(), "url", verificationUrl));
			log.info("verification Url : {}", verificationUrl);
			// Send email to user with verification URL
			// emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(),
			// verificationUrl, ACCOUNT.getType());
			user.setEnabled(false);
			user.setNotLocked(true);
			// Return the newly created user
			return user;
			// If any errors, throw exception with proper message
		} catch (Exception exception) {
			log.error(exception.getMessage());
			throw new ApiException("An error occured when registering a user, please try again.");
		}
	}

	@Override
	public Collection<User> list(int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User get(Long id) {
		try {
			User user = jdbc.queryForObject(SELECT_USER_BY_USERID_QUERY, Map.of("user_id", id), new UserRowMapper());
			return user;
		} catch (EmptyResultDataAccessException exception) {
			throw new ApiException("User with id" + id + " cannot be retrieved");
		} catch (Exception exception) {
			log.error("Error updating user details", exception); // Log the full exception
			throw new ApiException("An error occured inside getUser, please try again ");
		}
	}

	@Override
	public User update(User data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User updateUserDetails(UpdateUserForm user) {
		try {
			SqlParameterSource parameters = getUserDetailsSqlParametersSource(user);
			jdbc.update(UPDATE_USER_DETAILS_QUERY, parameters);
			log.info("inside updateUserdetails");
			return get(user.getUserId());
		} catch (EmptyResultDataAccessException exception) {
			throw new ApiException("No user found by id : " + user.getUserId());
		} catch (Exception ex) {
			throw new ApiException("An error occured when updating user details");
		}
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = getUserByEmail(email);
		if (user == null) {
			log.error("Login failure : User not found in the database");
			throw new UsernameNotFoundException("User not found in the database");
		} else {
			log.info("User found in the database : {}", email);
			return new CustomeUser(user, roleRepository.getRoleByUserId(user.getUserId()));

		}
	}

	@Override
	public User getUserByEmail(String email) {
		try {
			User user = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
			return user;
		} catch (EmptyResultDataAccessException exception) {
			throw new ApiException("Email cannot be empty or email has not been found");
		} catch (Exception exception) {
			throw new ApiException("An error occured inside getUserByEmail, please try again ");
		}
	}

	/* ------- TWO FACTORS VERIFICATIONS ------- */
	@Override
	public void sendVerificationCode(User user) {

		String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
		String verificationCode = randomAlphabetic(8).toUpperCase();
		try {
			jdbc.update(DELETE_VERIFICATION_CODE_BY_USERID_QUERY, Map.of("userId", user.getUserId()));
			jdbc.update(INSERT_VERIFICATION_CODE_QUERY,
					Map.of("userId", user.getUserId(), "code", verificationCode, "expirationDate", expirationDate));
			log.info("Verification code : {}", verificationCode);
			// sendSMS(user.getPhone(), "From: SecureInvoices \nVerification code : \n" +
			// verificationCode);
		} catch (Exception exception) {
			throw new ApiException("An error occured inside sendVerificationCode, please try again ");
		}

	}

	@Override
	public User verifyCode(String email, String code) {
		// check if the code submitted has expired
		if (isVerificationCodeExpired(code))
			throw new ApiException("This code has expired. Please login again");
		try {
			User userByEmail = this.getUserByEmail(email);
			User userByCode = jdbc.queryForObject(SELECT_USER_BY_CODE_USER_QUERY, Map.of("code", code),
					new UserRowMapper());

			if (userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())) {
				// code verified. Has to be deleted next.
				jdbc.update(DELETE_VERIFICATION_CODE_BY_USERID_QUERY, Map.of("userId", userByCode.getUserId()));
				return userByCode;
			} else {
				throw new ApiException("Code is invalid. Please try again");
			}
		} catch (EmptyResultDataAccessException exception) {
			throw new ApiException("No user found by code : " + code);
		} catch (Exception exception) {
			throw new ApiException("An error occured inside verifyCode, please try again ");
		}
	}

	private boolean isVerificationCodeExpired(String code) {
		try {
			return jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code), Boolean.class);

		} catch (EmptyResultDataAccessException exception) {
			throw new ApiException("Code is invalid. Please try again !");
		} catch (Exception exception) {
			throw new ApiException("An error occured inside verifyCode, please try again ");
		}
	}

	/* ------- END TWO FACTORS VERIFICATIONS ------- */

	/* ------- RESET PASSWORD ------- */

	@Override
	public void resetPassword(String email) {
		if (getEmailCount(email.trim().toLowerCase()) <= 0)
			throw new ApiException("There is no account for this email address.");

		try {
			String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
			User user = getUserByEmail(email);
			String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
			// Save URL in reset password verification table
			jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USERID_QUERY, Map.of("userId", user.getUserId()));
			jdbc.update(INSERT_URL_PASSWORD_VERIFICATION_CODE_QUERY,
					Map.of("userId", user.getUserId(), "url", verificationUrl, "expirationDate", expirationDate));
			log.info("verification Url : {}", verificationUrl);
			// TODO send email with url to user
		} catch (Exception exception) {
			throw new ApiException("An error occured inside resetPassword, please try again");
		}
	}

	@Override
	public User verifyPasswordKey(String key) {
		// check if the url used to change password is expired or not.
		if (isUrlExpired(key, PASSWORD))
			throw new ApiException("This url has expired. Please try again");
		try {
			User user = jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY,
					Map.of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
			// jdbc.update(DELETE_PASSWORD_URL_BY_USERID_QUERY, Map.of("userId",
			// user.getUserId()));
			return user;
		} catch (EmptyResultDataAccessException exception) {
			log.error(exception.getMessage());
			throw new ApiException("This url is not valid. Please try again");
		} catch (Exception exception) {
			throw new ApiException("An error occured inside verifyPasswordKey, please try again ");
		}
	}

	@Override
	public void renewPassword(String key, String password, String confirmPassword) {

		if (!password.equals(confirmPassword))
			throw new ApiException("Passwords do not match. Please try again");
		try {
			jdbc.update(UPDATE_USER_PASSWORD_BY_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType()),
					"password", encoder.encode(confirmPassword)));
			jdbc.update(DELETE_VERIFICATION_BY_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())));
		} catch (EmptyResultDataAccessException exception) {
			throw new ApiException("This url is not valid. Please try again");
		} catch (Exception exception) {
			throw new ApiException("An error occured inside renewPassword, please try again ");
		}
	}

	/* ------- UPDATE USER PASSWORD ------- */
	
	@Override
	public void verifyPassword(Long userId, String password) {
		User user = get(userId);
		if (!encoder.matches(password, user.getPassword())) {
		    throw new ApiException("Invalid password. Please try again.");
		}	
	}
	
	@Override
	public void udpatePassword(Long userId, String password, String newPassword, String confirmPassword) {
		if (!newPassword.equals(confirmPassword)) {
			throw new ApiException("Password don't match. Please try again.");
		}
		log.info("current password : {}", password);
		User user = get(userId);
		if (!encoder.matches(password, user.getPassword())) {
		    throw new ApiException("Invalid password. Please try again.");
		}
		try {
			jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY,
					Map.of("userId", userId, "password", encoder.encode(newPassword)));
		} catch (Exception exception) {
			log.error(exception.getMessage());
			throw new ApiException("An error occured inside updatePassword, please try again ");
		}
	}
	
	/* ------- UPDATE ACCOUNT SETTINGS ------- */
	
	@Override
	public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
		log.info("Updating account settings for user id : {}", userId);
		try {
			jdbc.update(UPDATE_USER_SETTINGS_QUERY, Map.of("enabled", enabled, "userId", userId, "notLocked", notLocked));
		} catch (Exception exception) {
			throw new ApiException("An error occured inside updateAccountSettings, please try again ");
		}
	}
	
	/* ------- UPDATE AUTHENTICATION SETTINGS ------- */
	@Override
	public void updateAuthenticationSettings(Long userId, @Valid Boolean isUsingMfa) {
		log.info("Updating authentication settings for user id : {}", userId);
		try {
			jdbc.update(UPDATE_USER_AUTHENTICATION_SETTINGS_QUERY, Map.of("isUsingMfa", isUsingMfa, "userId", userId));
		} catch (Exception exception) {
			log.error(exception.getMessage());
			throw new ApiException("An error occured inside updateAuthenticationSettings, please try again ");
		}
	}

	/* ------- VERIFY ACCOUNT USER ------- */

	@Override
	public User verifyAccountKey(String key) {
		try {
			User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_URL_QUERY,
					Map.of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
			jdbc.update(UPDATE_USER_ENABLED_QUERY, Map.of("enabled", true, "userId", user.getUserId()));
			return user;
		} catch (EmptyResultDataAccessException exception) {
			log.error(exception.getMessage());
			throw new ApiException("This url is not valid. Please try again");
		} catch (Exception exception) {
			throw new ApiException("An error occured inside verifyAccountKey, please try again ");
		}
	}

	/* Private methods */

	private Boolean isUrlExpired(String key, VerificationType password) {
		try {
			return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL_QUERY,
					Map.of("url", getVerificationUrl(key, password.getType())), Boolean.class);

		} catch (EmptyResultDataAccessException exception) {
			log.error(exception.getMessage());
			throw new ApiException("This url is not valid. Please try again");
		} catch (Exception exception) {
			throw new ApiException("An error occured inside isUrlExpired, please try again ");
		}
	}

	private Integer getEmailCount(String email) {
		return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
	}

	private String getVerificationUrl(String key, String type) {
		// return url for the server for the dev
		return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/" + type + "/" + key)
				.toUriString();
	}

	private SqlParameterSource getSqlParametersSource(User user) {
		return new MapSqlParameterSource().addValue("firstName", user.getFirstName())
				.addValue("lastName", user.getLastName()).addValue("email", user.getEmail())
				.addValue("password", encoder.encode(user.getPassword()));
	}

	private SqlParameterSource getUserDetailsSqlParametersSource(UpdateUserForm user) {
		log.info("User parsed: userId = {}", user.getAddress());
		return new MapSqlParameterSource().addValue("user_id", user.getUserId())
				.addValue("firstName", user.getFirstName()).addValue("lastName", user.getLastName())
				.addValue("email", user.getEmail()).addValue("phone", user.getPhone())
				.addValue("address", user.getAddress()).addValue("title", user.getTitle())
				.addValue("bio", user.getBio());
	}

	/* ------- UPDATE USER IMAGE ------- */
	
	@Override
	public void updateImage(UserDTO user, MultipartFile image) {
		String userImageUrl = getUserImageUrl(user.getEmail());
		user.setImageUrl(userImageUrl);
		saveImage(user.getEmail(), image);
		try {
			jdbc.update(UPDATE_USER_IMAGE_QUERY, Map.of("imageUrl", userImageUrl, "userId", user.getUserId()));
		} catch(Exception exception) {
			log.error(exception.getMessage());
			throw new ApiException("An error occured inside updateImage, please try again");
		}
		
	}

	private void saveImage(String email, MultipartFile image) {
	    // Define the base directory for storing images
        Path fileStorageLocation = Paths.get(imagePath).toAbsolutePath().normalize();
	    try {
	        // Ensure the directories exist
	        if (!Files.exists(fileStorageLocation)) {
	            Files.createDirectories(fileStorageLocation);
	        }
	        // Save the image
	        Path targetLocation = fileStorageLocation.resolve(email + ".png");
	        Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException exception) {
	        log.error("Failed to save image: {}", exception.getMessage());
	        throw new ApiException("Unable to save the image");
	    }
	}


	private String getUserImageUrl(String email) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/user/image/")
				.path(email)
				.path(".png")
				.toUriString(); 
	}
}
