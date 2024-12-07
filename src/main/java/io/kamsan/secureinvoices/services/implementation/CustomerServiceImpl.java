package io.kamsan.secureinvoices.services.implementation;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.entities.Invoice;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.repositories.CustomerRepository;
import io.kamsan.secureinvoices.repositories.InvoiceRepository;
import io.kamsan.secureinvoices.services.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final InvoiceRepository invoiceRepository;

	@Override
	public Customer createCustomer(Customer customer) {
		customer.setCreatedAt(LocalDateTime.now());
		return customerRepository.save(customer);
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public Page<Customer> getCustomers(int page, int size) {
		return customerRepository.findAll(PageRequest.of(page, size));
	}

	@Override
	public Iterable<Customer> getCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public Customer getCustomer(long id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new ApiException("Customer with id " + id + " has not been found"));
	}

	@Override
	public Page<Customer> searchCustomers(String keyword, int page, int size) {
		return customerRepository.findByNameContaining(keyword, PageRequest.of(page, size));
	}

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
	public void addInvoiceToCustomer(Long customerId, Long invoiceId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new ApiException("Customer with id " + customerId + " has not been found"));

		Invoice invoice = invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new ApiException("Invoice with id " + invoiceId + " has not been found"));

		// Set the customer on the invoice (owning side)
		invoice.setCustomer(customer);

	}

}
