package io.kamsan.secureinvoices.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.kamsan.secureinvoices.entities.invoices.Invoice;
import io.kamsan.secureinvoices.enums.InvoiceStatusEnum;

@Repository
public interface InvoiceRepository
		extends PagingAndSortingRepository<Invoice, Long>, ListCrudRepository<Invoice, Long> {
	
	 @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.issuedAt BETWEEN :startDate AND :endDate ORDER BY i.issuedAt DESC")
	    Page<Invoice> findByStatusAndDateRange(@Param("status") InvoiceStatusEnum status, 
	                                           @Param("startDate") LocalDateTime startDate, 
	                                           @Param("endDate") LocalDateTime endDate, Pageable pageable);
	 
	 
	 
}
