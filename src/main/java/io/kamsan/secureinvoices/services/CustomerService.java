package io.kamsan.secureinvoices.services;

import org.springframework.data.domain.Page;

import io.kamsan.secureinvoices.domain.Statistic;
import io.kamsan.secureinvoices.entities.Customer;

public interface CustomerService { 
	// Customer functions
	Customer createCustomer(Customer customer);
	Customer updateCustomer(Customer customer);
	Page<Customer> getCustomers(int page, int size);
	Iterable<Customer> getCustomers();
	Customer getCustomer(long id);
	Page<Customer> searchCustomers(String keyword, int page, int size);
	Statistic getStats();

}
