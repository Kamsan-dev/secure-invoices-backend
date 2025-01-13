package io.kamsan.secureinvoices.query;

public class StatisticQuery {
	
	public final String STATS_QUERY = "SELECT " +
            "(SELECT ROUND(SUM(total)) FROM Invoice WHERE status = 'Paid') AS total_billed, " +
            "(SELECT COUNT(*) FROM Customer) AS total_customers, " +
            "(SELECT COUNT(*) FROM Invoice) AS total_invoices";
	
	public static final String MOUNTHLY_INVOICE_STATS_QUERY = "SELECT \r\n"
			+ "    DATE_FORMAT(i.issued_at, '%M %Y') AS month,\r\n"
			+ "    i.status AS status, \r\n"
			+ "    COUNT(i.invoice_id) AS invoice_count\r\n"
			+ "FROM Invoice i\r\n"
			+ "WHERE i.issued_at >= CURDATE() - INTERVAL 6 MONTH \r\n"
			+ "GROUP BY YEAR(i.issued_at), MONTH(i.issued_at), i.status, DATE_FORMAT(i.issued_at, '%M %Y')\r\n"
			+ "ORDER BY YEAR(i.issued_at) DESC, MONTH(i.issued_at) DESC";

}
