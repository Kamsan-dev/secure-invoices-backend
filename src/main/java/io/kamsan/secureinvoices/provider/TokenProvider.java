package io.kamsan.secureinvoices.provider;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import io.kamsan.secureinvoices.domain.CustomeUser;
import io.kamsan.secureinvoices.dtomapper.UserDTOMapper;
import io.kamsan.secureinvoices.services.RoleService;
import io.kamsan.secureinvoices.services.UserService;
import io.kamsan.secureinvoices.services.implementation.RoleServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;



@Component
@RequiredArgsConstructor
public class TokenProvider {
	
	private static final String SECUREINVOICES = "SecureInvoices";
	private static final String CUSTOMER_MANAGEMENT_SERVICES = "CUSTOMER_MANAGEMENT_SERVICES";
	private static final String AUTHORITIES = "authorities";
	// 30 min expiration
	private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000;
	// 5 days expiration
	private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;
	private final UserService userService;
	private final RoleService roleService;
	
	@Value("${jwt.secret}")
	private String secret;
	
	public String createAccessToken(CustomeUser customeUser) {
		String[] claims = getClaimsFromUser(customeUser);
		return JWT
				.create()
				.withIssuer(SECUREINVOICES)
				.withAudience(CUSTOMER_MANAGEMENT_SERVICES)
				.withIssuedAt(new Date())
				.withSubject(String.valueOf(customeUser.getUser().getUserId()))
				.withArrayClaim(AUTHORITIES, claims)
				.withExpiresAt(new Date(currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(secret.getBytes()));

	}
	
	public String createRefreshToken(CustomeUser customeUser) {
		return JWT
				.create()
				.withIssuer(SECUREINVOICES)
				.withAudience(CUSTOMER_MANAGEMENT_SERVICES)
				.withIssuedAt(new Date())
				.withSubject(String.valueOf(customeUser.getUser().getUserId()))
				.withExpiresAt(new Date(currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(secret.getBytes()));

	}

	
	public Long getSubject (String token, HttpServletRequest request) {
		try {
			return Long.valueOf(getJWTVerifier().verify(token).getSubject());
		} catch (TokenExpiredException exception) {
			request.setAttribute("expiredMessage", exception.getMessage());
			throw exception;
		} catch (InvalidClaimException exception) {
			request.setAttribute("invalidClaim", exception.getMessage());
			throw exception;
		} catch (Exception exception) {
			throw exception;
		}
	}
	
	public Authentication getAuthentication(Long userId, 
			List<GrantedAuthority> authorities, HttpServletRequest request) {
		UserDTO userDTO = this.userService.getUserById(userId);
		User user = UserDTOMapper.fromUserDTO(userDTO);
		Role role = roleService.getRoleByUserId(user.getUserId());
		CustomeUser customeUser = new CustomeUser(user, role);
		UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(customeUser, null, authorities);
		userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		return userPasswordAuthToken;
	}
	
	public boolean isTokenValid(Long userId, String token) {
		JWTVerifier verifier = getJWTVerifier();
		return !Objects.isNull(userId) && !isTokenExpired(verifier, token);
	}
	
	private boolean isTokenExpired(JWTVerifier verifier, String token) {
		Date expiration = verifier.verify(token).getExpiresAt();
		// Check if expiration date of token is before today : true of false
		return (expiration.before(new Date()));
	}
	private String[] getClaimsFromUser(CustomeUser customeUser) {
		return customeUser.getAuthorities()
				.stream()
				.map(authority -> authority.getAuthority())
				.toArray(String[]::new);
	}
	
	public List<GrantedAuthority> getAuthoritiesFromToken(String token){
		String[] claims = getClaimsFromToken(token);
		return stream(claims).map(SimpleGrantedAuthority::new).collect(toList());		
	}

	// claims example : --customer:delete, customer:update
	private String[] getClaimsFromToken(String token) {
		JWTVerifier verifier = getJWTVerifier();
		return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
	}

	/* DECODE JWT Token */
	private JWTVerifier getJWTVerifier() {
		JWTVerifier verifier;
		try {
			Algorithm algorithm = Algorithm.HMAC512(secret);
			verifier = JWT.require(algorithm).withIssuer(SECUREINVOICES).build();
		} catch (JWTVerificationException exception) {
			throw new JWTVerificationException("Token cannot be verified");
		}
		
		return verifier;
	}

}
