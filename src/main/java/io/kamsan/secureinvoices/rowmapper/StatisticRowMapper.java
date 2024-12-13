package io.kamsan.secureinvoices.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import io.kamsan.secureinvoices.domain.Statistic;

public class StatisticRowMapper implements RowMapper<Statistic>{

	@Override
	public Statistic mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Statistic.builder()
				.totalCustomers(rs.getInt("total_customers"))
				.totalInvoices(rs.getInt("total_invoices"))
				.totalBilled(rs.getDouble("total_billed"))
				.build();
	}
}
