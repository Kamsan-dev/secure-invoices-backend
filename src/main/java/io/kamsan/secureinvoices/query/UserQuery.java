package io.kamsan.secureinvoices.query;

public class UserQuery {

	public static final String INSERT_USER_QUERY = "INSERT INTO Users (first_name, last_name, email, password) "
			+ "VALUES (:firstName, :lastName, :email, :password)";
	public static final String COUNT_USER_EMAIL_QUERY = "SELECT Count(*) FROM Users WHERE email = :email";
	public static final String INSERT_ACCOUNT_VERIFICATION_QUERY = "INSERT INTO AccountVerification (user_id, url) VALUES (:userId, :url)";
	public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM Users WHERE email = :email";
	public static final String DELETE_VERIFICATION_CODE_BY_USERID_QUERY = "DELETE From TwoFactorVerifications WHERE user_id = :userId";
	public static final String INSERT_VERIFICATION_CODE_QUERY = 
			"INSERT INTO TwoFactorVerifications (user_id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";
	public static final String SELECT_USER_BY_CODE_USER_QUERY = 
			"SELECT * FROM Users WHERE user_id = (SELECT user_id from TwoFactorVerifications WHERE code = :code)";
	public static final String SELECT_CODE_EXPIRATION_QUERY = 
	"SELECT expiration_date < NOW() FROM TwoFactorVerifications WHERE code = :code";
	
	public static final String DELETE_PASSWORD_VERIFICATION_BY_USERID_QUERY = "DELETE From ResetPasswordVerification "
			+ "WHERE user_id = :userId";
	
	public static final String INSERT_URL_PASSWORD_VERIFICATION_CODE_QUERY = "INSERT INTO ResetPasswordVerification (user_id, url, expiration_date) VALUES (:userId, :url, :expirationDate)";

	public static final String SELECT_EXPIRATION_BY_URL_QUERY = 
			"SELECT expiration_date < NOW() FROM ResetPasswordVerification WHERE url = :url";
	
	public static final String SELECT_USER_BY_PASSWORD_URL_QUERY = 
			"SELECT * FROM Users WHERE user_id = (SELECT user_id from ResetPasswordVerification WHERE url = :url)";
	
	public static final String DELETE_PASSWORD_URL_BY_USERID_QUERY = "DELETE FROM ResetPasswordVerification where user_id = :userId";

	public static final String UPDATE_USER_PASSWORD_BY_URL_QUERY = 
			"UPDATE Users SET password = :password WHERE user_id = (SELECT user_id from ResetPasswordVerification WHERE url = :url)";

	public static final String DELETE_VERIFICATION_BY_URL_QUERY = "DELETE FROM ResetPasswordVerification where url = :url";


	public static final String SELECT_USER_BY_ACCOUNT_URL_QUERY = 
			"SELECT * FROM Users WHERE user_id = (SELECT user_id from AccountVerification WHERE url = :url)";

	public static final String UPDATE_USER_ENABLED_QUERY = "UPDATE Users SET enabled = :enabled WHERE user_id = :userId";
}