package io.kamsan.secureinvoices.dtomapper;

import org.springframework.beans.BeanUtils;

import io.kamsan.secureinvoices.dtos.customers.CustomerDTO;
import io.kamsan.secureinvoices.entities.Customer;

public class CustomerDTOMapper {

	public static CustomerDTO fromCustomer(Customer customer) {
		CustomerDTO customerDTO = new CustomerDTO();
		BeanUtils.copyProperties(customer, customerDTO);
		customerDTO.setStatus(customer.getStatus().getLabel());
		customerDTO.setType(customer.getType().getLabel());
		return customerDTO;
	}
}
