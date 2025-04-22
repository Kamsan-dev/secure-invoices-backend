package io.kamsan.secureinvoices.services;

import org.springframework.data.domain.Page;

import io.kamsan.secureinvoices.domain.Statistic;
import io.kamsan.secureinvoices.dtos.customers.CustomerDTO;
import io.kamsan.secureinvoices.entities.Customer;

public interface CustomerService { 
	// Customer functions
	Customer createCustomer(Customer customer);
	Customer updateCustomer(Customer customer);
	Page<CustomerDTO> getCustomers(int page, int size);
	Iterable<Customer> getCustomers();
	Customer getCustomer(long id);
	Page<CustomerDTO> searchCustomers(String name, String type, String status, int page, int size);
	Statistic getStats();

}
