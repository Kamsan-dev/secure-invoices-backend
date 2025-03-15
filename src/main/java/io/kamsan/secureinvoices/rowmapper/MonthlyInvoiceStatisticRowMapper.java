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
		statistic.setStatus(capitalizeFirstLetter(rs.getString("status")));
		statistic.setInvoiceCount(rs.getInt("invoice_count"));
		return statistic;
	}
	
	public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the same string if null or empty
        }
        
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
