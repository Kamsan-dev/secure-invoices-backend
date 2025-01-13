package io.kamsan.secureinvoices.services.implementation;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.entities.Invoice;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.repositories.CustomerRepository;
import io.kamsan.secureinvoices.repositories.InvoiceRepository;
import io.kamsan.secureinvoices.services.InvoiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final CustomerRepository customerRepository;

	@Override
	public Invoice createInvoice(Invoice invoice) {
		invoice.setIssuedAt(LocalDateTime.now());
		invoice.setInvoiceNumber(randomAlphanumeric(8).toUpperCase());
		return invoiceRepository.save(invoice);
	}

	@Override
	public Page<Invoice> getInvoices(int page, int size) {
		return invoiceRepository.findAll(PageRequest.of(page, size));
	}

	@Transactional
	@Override
	public void addInvoiceToCustomer(Long customerId, Invoice invoice) {
		invoice.setInvoiceNumber(randomAlphanumeric(8).toUpperCase());
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new ApiException("Customer with id " + customerId + " has not been found"));
		// Set the customer on the invoice (owning side)
		invoice.setCustomer(customer);
		invoiceRepository.save(invoice);

	}

	@Override
	public Invoice getInvoice(Long id) {
		return invoiceRepository.findById(id)
				.orElseThrow(() -> new ApiException("Invoice with id " + id + " has not been found"));
	}

	@Override
	public Page<Invoice> getMonthlyStatusInvoices(String status, String date_range, int page, int size) {
		
        // Use a formatter with an explicit locale to convert MMMM yyyy into LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        // Parse Month-Year into YearMonth
        YearMonth yearMonth = YearMonth.parse(date_range, formatter);
        // Convert to LocalDateTime
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        System.out.println("Start DateTime: " + startDateTime);
        System.out.println("End DateTime: " + endDateTime);
		return invoiceRepository.findByStatusAndDateRange(status, startDateTime, endDateTime, PageRequest.of(page, size));
	}

}
