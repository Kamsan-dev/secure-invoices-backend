package io.kamsan.secureinvoices.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.enums.customers.CustomerStatusEnum;
import io.kamsan.secureinvoices.enums.customers.CustomerTypeEnum;

@Repository
public interface CustomerRepository
		extends PagingAndSortingRepository<Customer, Long>, ListCrudRepository<Customer, Long> {
	Page<Customer> findByNameContaining(String name, Pageable pageable);

	 @Query("""
		        SELECT c FROM Customer c
		        WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
		          AND (:status IS NULL OR c.status = :status)
		          AND (:type IS NULL OR c.type = :type)
		    """)
		    Page<Customer> filterCustomers(
		        @Param("name") String name,
		        @Param("type") CustomerTypeEnum type,
		        @Param("status") CustomerStatusEnum status,
		        Pageable pageable
		    );
}
