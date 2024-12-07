package io.kamsan.secureinvoices.repositories;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import io.kamsan.secureinvoices.entities.Invoice;

@Repository
public interface InvoiceRepository
		extends PagingAndSortingRepository<Invoice, Long>, ListCrudRepository<Invoice, Long> {
}
