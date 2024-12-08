package io.kamsan.secureinvoices.services;

import org.springframework.data.domain.Page;

import io.kamsan.secureinvoices.entities.Invoice;

public interface InvoiceService {
	// Invoice functions
	Invoice createInvoice(Invoice invoice);
	Page<Invoice> getInvoices(int page, int size);
	void addInvoiceToCustomer(Long id, Invoice invoice);
	Invoice getInvoice(Long id);
}
