package io.kamsan.secureinvoices.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import io.kamsan.secureinvoices.entities.Role;

public class RoleRowMapper implements RowMapper<Role>{

	@Override
	public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Role.builder()
				.roleId(rs.getLong("role_id"))
				.name(rs.getString("name"))
				.permission(rs.getString("permission"))
				.build();
	}
}
