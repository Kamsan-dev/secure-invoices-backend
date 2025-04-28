package io.kamsan.secureinvoices.services.implementation;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.domain.Statistic;
import io.kamsan.secureinvoices.dtomapper.CustomerDTOMapper;
import io.kamsan.secureinvoices.dtos.customers.CustomerDTO;
import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.enums.customers.CustomerStatusEnum;
import io.kamsan.secureinvoices.enums.customers.CustomerTypeEnum;
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
	public CustomerDTO createCustomer(CustomerDTO customerDTO) {
	    Customer customer = CustomerDTOMapper.fromCustomerDTO(customerDTO);
	    customer.setCreatedAt(LocalDateTime.now());

	    Customer savedCustomer = customerRepository.save(customer);
	    log.info("Customer created with ID: {}", savedCustomer.getCustomerId());

	    return CustomerDTOMapper.fromCustomer(savedCustomer);
	}


	@Transactional
	@Override
	public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
		customerRepository.save(CustomerDTOMapper.fromCustomerDTO(customerDTO));
		log.info("Updated data of customer id {}", customerDTO.getCustomerId());
		return getCustomer(customerDTO.getCustomerId());
	}

	@Override
	public Page<CustomerDTO> getCustomers(int page, int size) {
		return customerRepository.findAll(PageRequest.of(page, size)).map(CustomerDTOMapper::fromCustomer);
	}

	@Override
	public Iterable<Customer> getCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public CustomerDTO getCustomer(long id) {
		return CustomerDTOMapper.fromCustomer(customerRepository.findById(id)
				.orElseThrow(() -> new ApiException("Customer with id " + id + " has not been found")));
	}

	@Override
	public Page<CustomerDTO> searchCustomers(String name, String type, String status, int page, int size) {
		//return customerRepository.findByNameContaining(keyword, PageRequest.of(page, size));
	    CustomerTypeEnum typeEnum = "ALL".equalsIgnoreCase(type) ? null : CustomerTypeEnum.valueOf(type.toUpperCase());
	    CustomerStatusEnum statusEnum = "ALL".equalsIgnoreCase(status) ? null : CustomerStatusEnum.valueOf(status.toUpperCase());
		Page<Customer> pageCustomer =  customerRepository.filterCustomers(name, typeEnum, statusEnum, PageRequest.of(page, size));
		
		return pageCustomer.map(CustomerDTOMapper::fromCustomer);
	}

	@Override
	public Statistic getStats() {
		return jdbc.queryForObject(new StatisticQuery().STATS_QUERY, Map.of(), new StatisticRowMapper());
	}

}
