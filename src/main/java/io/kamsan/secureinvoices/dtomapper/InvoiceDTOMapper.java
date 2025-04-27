package io.kamsan.secureinvoices.dtomapper;

import org.springframework.beans.BeanUtils;

import io.kamsan.secureinvoices.dtos.InvoiceDTO;
import io.kamsan.secureinvoices.entities.invoices.Invoice;

public class InvoiceDTOMapper {
	public static InvoiceDTO fromInvoice(Invoice invoice) {
		InvoiceDTO invoiceDTO = new InvoiceDTO();
		BeanUtils.copyProperties(invoice, invoiceDTO);
		invoiceDTO.setStatus(invoice.getStatus().getLabel());
		if (invoice.getCustomer() != null) {
			invoiceDTO.setCustomerId(invoice.getCustomer().getCustomerId());
		}
		return invoiceDTO;
	}
}
