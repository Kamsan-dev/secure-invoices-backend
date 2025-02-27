package io.kamsan.secureinvoices.constant;

public class Constants {

	// security
	
	public static final String[] PUBLIC_URLS = {"/user/register/**", "/user/login/**", "/user/verify/code/**", 
			"/user/resetpassword/**", "/user/verify/password/**", "/user/verify/account/**", "/user/refresh/token/**", "/user/image/**"};
	
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String AUTHORIZATION = "Authorization";
	public static final String[] PUBLIC_ROUTES = {"/user/register", "/user/login", "/user/verify/code", 
			"/user/refresh/token", "/user/image"};
	
	// token provider
	
	public static final String SECUREINVOICES = "SecureInvoices";
	public static final String CUSTOMER_MANAGEMENT_SERVICES = "CUSTOMER_MANAGEMENT_SERVICES";
	public static final String AUTHORITIES = "authorities";
	// 30 min expiration
	public static final long ACCESS_TOKEN_EXPIRATION_TIME = 432_000_000; //1_800_000;
	// 5 days expiration
	public static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;
}
