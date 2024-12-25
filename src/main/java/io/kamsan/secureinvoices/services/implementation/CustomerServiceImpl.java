package io.kamsan.secureinvoices.services.implementation;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.domain.Statistic;
import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.query.StatisticQuery;
import io.kamsan.secureinvoices.repositories.CustomerRepository;
import io.kamsan.secureinvoices.rowmapper.StatisticRowMapper;
import io.kamsan.secureinvoices.services.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final NamedParameterJdbcTemplate jdbc;

	@Override
	public Customer createCustomer(Customer customer) {
		customer.setCreatedAt(LocalDateTime.now());
		log.info("Customer created");
		return customerRepository.save(customer);
	}

	@Transactional
	@Override
	public Customer updateCustomer(Customer customer) {
		customerRepository.save(customer);
		log.info("customer id {}", customer.getCustomerId());
		return this.getCustomer(customer.getCustomerId());
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
	public Statistic getStats() {
		return jdbc.queryForObject(new StatisticQuery().STATS_QUERY, Map.of(), new StatisticRowMapper());
	}

}
