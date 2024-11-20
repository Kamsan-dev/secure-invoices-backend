package io.kamsan.secureinvoices.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import io.kamsan.secureinvoices.filter.CustomAuthorizationFilter;
import io.kamsan.secureinvoices.handler.CustomeAccessDeniedHandler;
import io.kamsan.secureinvoices.handler.CustomeAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final String[] PUBLIC_URLS = {"/user/register/**", "/user/login/**", "/user/verify/code/**", 
			"/user/resetpassword/**", "/user/verify/password/**", "/user/verify/account/**", "/user/refresh/token/**", "/user/image/**"};
	private final CustomeAccessDeniedHandler customeAccessDeniedHandler;
	private final CustomeAuthenticationEntryPoint customeAuthenticationEntryPoint;
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final CustomAuthorizationFilter customAuthorizationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		return http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(ar -> ar.requestMatchers(PUBLIC_URLS).permitAll())
				.authorizeHttpRequests(
						ar -> ar.requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasAuthority("DELETE:USER"))
				.authorizeHttpRequests(ar -> ar.requestMatchers(HttpMethod.DELETE, "/customer/delete/**")
						.hasAuthority("DELETE:CUSTOMER"))
				.exceptionHandling(eh -> eh.accessDeniedHandler(customeAccessDeniedHandler).authenticationEntryPoint(customeAuthenticationEntryPoint))
				.authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
				.addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();

	}
	

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		return new ProviderManager(daoAuthenticationProvider);
	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> web.ignoring()
	                       .requestMatchers("/v3/api-docs/**")
	                       .requestMatchers("configuration/**") 
	                       .requestMatchers("/swagger*/**")
	                       .requestMatchers("/webjars/**")
	                       .requestMatchers("/swagger-ui/**");
	}
	
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000", "http://secureinvoices.org"));
		//corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Jwt-Token", "Authorization", "Origin", "Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "File-Name"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}
}