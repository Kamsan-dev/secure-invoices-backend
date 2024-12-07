package io.kamsan.secureinvoices.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import io.kamsan.secureinvoices.entities.Customer;

@Repository
public interface CustomerRepository
		extends PagingAndSortingRepository<Customer, Long>, ListCrudRepository<Customer, Long> {
	Page<Customer> findByNameContaining(String name, Pageable pageable);
}
