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

	@Override
	public Customer createCustomer(Customer customer) {
		customer.setCreatedAt(LocalDateTime.now());
		log.info("Customer created");
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

}
