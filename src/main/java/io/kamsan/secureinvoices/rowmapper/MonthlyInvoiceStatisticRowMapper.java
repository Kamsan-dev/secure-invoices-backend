package io.kamsan.secureinvoices.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import io.kamsan.secureinvoices.domain.MonthlyInvoiceStatistic;

public class MonthlyInvoiceStatisticRowMapper implements RowMapper<MonthlyInvoiceStatistic> {

	@Override
	public MonthlyInvoiceStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
		MonthlyInvoiceStatistic statistic = new MonthlyInvoiceStatistic();
		statistic.setMonth(rs.getString("month"));
		statistic.setStatus(rs.getString("status"));
		statistic.setInvoiceCount(rs.getInt("invoice_count"));
		return statistic;
	}
}
