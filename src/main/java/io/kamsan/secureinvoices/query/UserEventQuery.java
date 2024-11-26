package io.kamsan.secureinvoices.query;

public class UserEventQuery {
	public static final String SELECT_EVENTS_BY_USER_ID = "SELECT e.event_id, e.type, e.description, "
			+ "ue.device, ue.ip_address, ue.occured_at "
	        + "FROM Events e "
	        + "INNER JOIN UserEvents ue ON e.event_id = ue.event_id "
	        + "WHERE ue.user_id = :userId "
	        + "ORDER BY ue.occured_at DESC LIMIT 5";
	
	public static final String INSERT_USER_EVENT_BY_EMAIL_QUERY = "INSERT INTO UserEvents "
	        + "(user_id, event_id, device, ip_address) "
	        + "VALUES ("
	        + "(SELECT user_id FROM Users WHERE email = :email), "
	        + "(SELECT event_id FROM Events WHERE type = :type), "
	        + ":device, :ipAddress)";

	
	public static final String INSERT_USER_EVENT_BY_USER_ID_QUERY = "INSERT INTO UserEvents "
	        + "(user_id, event_id, device, ip_address) "
	        + "VALUES ("
	        + "(SELECT user_id FROM Users u WHERE u.user_id = :userId), "
	        + "(SELECT event_id FROM Events e WHERE e.type = :type), "
	        + ":device, :ipAddress)";
}
