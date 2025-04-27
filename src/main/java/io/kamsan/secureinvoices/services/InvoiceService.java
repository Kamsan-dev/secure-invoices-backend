package io.kamsan.secureinvoices.services;

import org.springframework.data.domain.Page;

import io.kamsan.secureinvoices.dtos.InvoiceDTO;
import io.kamsan.secureinvoices.entities.invoices.Invoice;

public interface InvoiceService {
	// Invoice functions
	Invoice createInvoice(String description);
	Page<InvoiceDTO> getInvoices(int page, int size);
	Page<InvoiceDTO> getInvoicesByCustomerId(Long customerId, int page, int size);
	Page<InvoiceDTO> getMonthlyStatusInvoices(String status, String date_range, int page, int size);
	void addInvoiceToCustomer(Long id, Invoice invoice);
	InvoiceDTO getInvoice(Long id);
	InvoiceDTO update(Long id, InvoiceDTO invoice);
	void deleteInvoiceById(Long id);
}
