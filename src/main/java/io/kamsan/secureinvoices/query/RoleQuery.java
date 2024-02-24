package io.kamsan.secureinvoices.query;

public class RoleQuery {
	
	public static final String SELECT_ROLE_BY_NAME_QUERY = 
			"SELECT * FROM Roles WHERE name = :roleName";
	public static final String INSERT_ROLE_TO_USER_QUERY = 
			"INSERT INTO UserRoles (user_id, role_id)"
			+ " VALUES (:userId, :roleId)";
	
	public static final String SELECT_ROLE_BY_USERID_QUERY = "SELECT r.role_id, r.name, r.permission FROM Roles r JOIN UserRoles ur ON ur.role_id = r.role_id JOIN Users u ON u.user_id = ur.user_id WHERE u.user_id = :userId";

}
