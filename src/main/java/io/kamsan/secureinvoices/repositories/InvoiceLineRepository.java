package io.kamsan.secureinvoices.repositories;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import io.kamsan.secureinvoices.entities.invoices.InvoiceLine;

@Repository
public interface InvoiceLineRepository
		extends PagingAndSortingRepository<InvoiceLine, Long>, ListCrudRepository<InvoiceLine, Long> {
	
}
