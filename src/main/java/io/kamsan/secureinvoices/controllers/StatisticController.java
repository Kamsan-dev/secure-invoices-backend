package io.kamsan.secureinvoices.controllers;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.kamsan.secureinvoices.domain.HttpResponse;
import io.kamsan.secureinvoices.services.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/statistic")
@RequiredArgsConstructor
@Slf4j
public class StatisticController {

	private final StatisticService statisticService;

	@GetMapping("/mounthly-statistic-invoice")
	public ResponseEntity<HttpResponse> mounthlyStatisticInvoice() {
		return ResponseEntity.ok()
				.body(HttpResponse.builder().timeStamp(now().toString())
						.data(of("stats", statisticService.getMounthlyInvoiceStatistic())).message("Profile retrieved")
						.status(HttpStatus.OK).statusCode(HttpStatus.OK.value()).build());
	}
	
	@GetMapping("/invoices/status-distribution")
	public ResponseEntity<HttpResponse> getInvoicesStatusDistribution() {
		return ResponseEntity.ok()
				.body(HttpResponse.builder().timeStamp(now().toString())
						.data(of("stats", statisticService.getInvoicesByStatus())).message("Profile retrieved")
						.status(HttpStatus.OK).statusCode(HttpStatus.OK.value()).build());
	}

}
