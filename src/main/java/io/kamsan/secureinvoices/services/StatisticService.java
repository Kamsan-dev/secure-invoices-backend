package io.kamsan.secureinvoices.services;

import java.util.List;

import io.kamsan.secureinvoices.domain.MonthlyInvoiceStatistic;

public interface StatisticService {
	
	public List<MonthlyInvoiceStatistic> getMounthlyInvoiceStatistic();

}
