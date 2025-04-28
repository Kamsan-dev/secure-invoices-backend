package io.kamsan.secureinvoices.services;

import org.springframework.data.domain.Page;

import io.kamsan.secureinvoices.domain.Statistic;
import io.kamsan.secureinvoices.dtos.customers.CustomerDTO;
import io.kamsan.secureinvoices.entities.Customer;

public interface CustomerService { 
	// Customer functions
	CustomerDTO createCustomer(CustomerDTO customerDTO);
	CustomerDTO updateCustomer(CustomerDTO customerDTO);
	Page<CustomerDTO> getCustomers(int page, int size);
	Iterable<Customer> getCustomers();
	CustomerDTO getCustomer(long id);
	Page<CustomerDTO> searchCustomers(String name, String type, String status, int page, int size);
	Statistic getStats();

}
