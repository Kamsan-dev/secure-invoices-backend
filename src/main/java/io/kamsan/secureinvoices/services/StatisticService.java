package io.kamsan.secureinvoices.services;

import java.util.List;

import io.kamsan.secureinvoices.domain.MonthlyInvoiceStatistic;
import io.kamsan.secureinvoices.dtos.stats.InvoiceStatusCountDTO;

public interface StatisticService {
	
	public List<MonthlyInvoiceStatistic> getMounthlyInvoiceStatistic();
	List<InvoiceStatusCountDTO> getInvoicesByStatus();

}
