package io.kamsan.secureinvoices.services.implementation;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.domain.MonthlyInvoiceStatistic;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.rowmapper.MonthlyInvoiceStatisticRowMapper;
import io.kamsan.secureinvoices.services.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static io.kamsan.secureinvoices.query.StatisticQuery.*;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
	private final NamedParameterJdbcTemplate jdbc;

	@Override
	public List<MonthlyInvoiceStatistic> getMounthlyInvoiceStatistic() {
		try {
			return jdbc.query(MOUNTHLY_INVOICE_STATS_QUERY, Map.of(), new MonthlyInvoiceStatisticRowMapper());
		} catch (Exception ex) {
			log.error(ex.getMessage());
			throw new ApiException("An error occured when retrieving mountly statistics invoices");
		}
	}
}
