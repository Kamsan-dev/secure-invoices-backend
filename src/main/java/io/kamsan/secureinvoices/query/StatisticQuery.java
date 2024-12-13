package io.kamsan.secureinvoices.query;

public class StatisticQuery {
	
	public final String STATS_QUERY = "SELECT " +
            "(SELECT ROUND(SUM(total)) FROM Invoice WHERE status = 'Paid') AS total_billed, " +
            "(SELECT COUNT(*) FROM Customer) AS total_customers, " +
            "(SELECT COUNT(*) FROM Invoice) AS total_invoices";

}
