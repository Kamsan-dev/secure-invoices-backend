package io.kamsan.secureinvoices.repositories.implementation;

import static io.kamsan.secureinvoices.enums.RoleType.ROLE_USER;
import static io.kamsan.secureinvoices.enums.VerificationType.ACCOUNT;
import static io.kamsan.secureinvoices.query.RoleQuery.INSERT_ROLE_TO_USER_QUERY;
import static io.kamsan.secureinvoices.query.RoleQuery.SELECT_ROLE_BY_NAME_QUERY;
import static io.kamsan.secureinvoices.query.UserQuery.*;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.kamsan.secureinvoices.domain.CustomeUser;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.repositories.RoleRepository;
import io.kamsan.secureinvoices.repositories.UserRepository;
import io.kamsan.secureinvoices.rowmapper.RoleRowMapper;
import io.kamsan.secureinvoices.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService{
	
	private final NamedParameterJdbcTemplate jdbc;
	private final RoleRepository<Role> roleRepository;
	private final BCryptPasswordEncoder encoder;

	@Override
	public User create(User user) {
		// Check the email is unique
		if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0) throw new ApiException("Email already in use. Please use a different email and try again");
		// Save new user
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			/* creating parameters for SQL Query */
			SqlParameterSource parameters = getSqlParametersSource(user);
			jdbc.update(INSERT_USER_QUERY,parameters, holder);
			user.setUserId(Objects.requireNonNull(holder.getKey().longValue()));
			// Add role to the user
			roleRepository.addRoleToUser(user.getUserId(), ROLE_USER.toString());
			// Send verification url
			String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
			// Save URL in verification table
			jdbc.update(INSERT_ACCOUNT_VERIFICATION_QUERY,
					Map.of("userId", user.getUserId(), "url", verificationUrl));
			// Send email to user with verification URL
			//emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT.getType());
			user.setEnabled(false);
			user.setNotLocked(true);
			// Return the newly created user
			return user;
			// If any errors, throw exception with proper message
		} catch (Exception exception) {
			log.error(exception.getMessage());
			throw new ApiException("An error occured, please try again LOL");
		}
	}

	@Override
	public Collection<User> list(int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User get(Long id) {
		// TODO Auto-generated method stub
		return null;
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
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = getUserByEmail(email);
		if (user == null) {
			log.error("Login failure : User not found in the database");
			throw new UsernameNotFoundException("User not found in the database");
		} else {
			log.info("User found in the database : {}", email);
			return new CustomeUser(user, 
					roleRepository.getRoleByUserId(user.getUserId()));
			
		}
	}

	@Override
	public User getUserByEmail(String email) {
		try {
			User user = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
			return user;
		} catch (EmptyResultDataAccessException exception) {
			throw new ApiException("No user found by email : " + email);
		} catch (Exception exception) {
			throw new ApiException("An error occured inside getUserByEmail, please try again ");
		}
	}
	
	/* Private methods */
	
	private Integer getEmailCount(String email) {
		return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
	}
	

	private SqlParameterSource getSqlParametersSource(User user) {
		return new MapSqlParameterSource()
				.addValue("firstName", user.getFirstName())
				.addValue("lastName", user.getLastName())
				.addValue("email", user.getEmail())
				.addValue("password", encoder.encode(user.getPassword())); 
	}
	
	private String getVerificationUrl(String key, String type) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/user/verify/" + type + "/" + key).toUriString();
	}

}
