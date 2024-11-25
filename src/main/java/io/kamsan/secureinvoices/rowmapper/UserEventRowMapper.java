package io.kamsan.secureinvoices.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import io.kamsan.secureinvoices.entities.UserEvent;

public class UserEventRowMapper implements RowMapper<UserEvent>{

	@Override
	public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserEvent.builder()
                .eventId(rs.getLong("event_id"))
                .type(rs.getString("type"))
                .description(rs.getString("description"))
                .device(rs.getString("device"))
                .ipAddress(rs.getString("ip_address"))
                .occuredAt(rs.getTimestamp("occured_at").toLocalDateTime())
                .build();
	}
}
