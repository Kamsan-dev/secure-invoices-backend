package io.kamsan.secureinvoices.services;

import org.springframework.data.domain.Page;

import io.kamsan.secureinvoices.entities.invoices.Invoice;
import io.kamsan.secureinvoices.entities.invoices.InvoiceLine;

public interface InvoiceService {
	// Invoice functions
	Invoice createInvoice(Invoice invoice);
	Page<Invoice> getInvoices(int page, int size);
	Page<Invoice> getMonthlyStatusInvoices(String status, String date_range, int page, int size);
	void addInvoiceToCustomer(Long id, Invoice invoice);
	Invoice getInvoice(Long id);
	Invoice update(Long id, Invoice invoice);
}
