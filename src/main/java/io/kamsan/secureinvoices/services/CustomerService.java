package io.kamsan.secureinvoices.services;

import org.springframework.data.domain.Page;

import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.entities.Invoice;

public interface CustomerService { 
	// Customer functions
	Customer createCustomer(Customer customer);
	Customer updateCustomer(Customer customer);
	Page<Customer> getCustomers(int page, int size);
	Iterable<Customer> getCustomers();
	Customer getCustomer(long id);
	Page<Customer> searchCustomers(String keyword, int page, int size);
	
	// Invoice functions
	Invoice createInvoice(Invoice invoice);
	Page<Invoice> getInvoices(int page, int size);
	void addInvoiceToCustomer(Long id, Long invoiceId);
}
