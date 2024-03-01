package io.kamsan.secureinvoices.configuration;

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
			"/user/resetpassword/**", "/user/verify/password/**"};
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
}
