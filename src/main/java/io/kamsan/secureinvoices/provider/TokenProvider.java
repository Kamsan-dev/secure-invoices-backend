package io.kamsan.secureinvoices.provider;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.Date;
import java.util.List;

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
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;



@Component
public class TokenProvider {
	
	private final String SECUREINVOICES = "SecureInvoices";
	private final String CUSTOMER_MANAGEMENT_SERVICES = "CUSTOMER_MANAGEMENT_SERVICES";
	private final String AUTHORITIES = "authorities";
	// 30 min expiration
	private final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000;
	// 5 days expiration
	private final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;
	
	@Value("${jwt.secret}")
	private String secret;
	
	public String createAccessToken(CustomeUser customeUser) {
		String[] claims = getClaimsFromUser(customeUser);
		return JWT
				.create()
				.withIssuer(SECUREINVOICES)
				.withAudience(CUSTOMER_MANAGEMENT_SERVICES)
				.withIssuedAt(new Date())
				.withSubject(customeUser.getUsername())
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
				.withSubject(customeUser.getUsername())
				.withExpiresAt(new Date(currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(secret.getBytes()));

	}

	
	public String getSubject (String token, HttpServletRequest request) {
		try {
			return getJWTVerifier().verify(token).getSubject();
			
		} catch (TokenExpiredException exception) {
			request.setAttribute("expiredMessage", exception.getMessage());
			
		} catch (InvalidClaimException exception) {
			request.setAttribute("invalidClaim", exception.getMessage());
		} catch (Exception exception) {
			throw exception;
		}
		
		return null;
	}
	
	public Authentication getAuthentication(String email, 
			List<GrantedAuthority> authorities, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
		userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		return userPasswordAuthToken;
	}
	
	public boolean isTokenValid(String email, String token) {
		JWTVerifier verifier = getJWTVerifier();
		return StringUtils.isNotEmpty(email) && !isTokenExpired(verifier, token);
	}
	
	private boolean isTokenExpired(JWTVerifier verifier, String token) {
		Date expiration = verifier.verify(token).getExpiresAt();
		// Check if expiration date of token is before today : true of false
		return expiration.before(new Date());
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
