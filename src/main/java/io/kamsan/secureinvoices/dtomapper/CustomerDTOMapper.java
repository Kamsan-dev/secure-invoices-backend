package io.kamsan.secureinvoices.dtomapper;

import org.springframework.beans.BeanUtils;

import io.kamsan.secureinvoices.dtos.customers.CustomerDTO;
import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.enums.customers.CustomerStatusEnum;
import io.kamsan.secureinvoices.enums.customers.CustomerTypeEnum;
import org.apache.commons.lang3.EnumUtils;

public class CustomerDTOMapper {

	public static CustomerDTO fromCustomer(Customer customer) {
		CustomerDTO customerDTO = new CustomerDTO();
		BeanUtils.copyProperties(customer, customerDTO);
		customerDTO.setStatus(customer.getStatus().getLabel());
		customerDTO.setType(customer.getType().getLabel());
		return customerDTO;
	}
	
	public static Customer fromCustomerDTO(CustomerDTO customerDTO) {
		Customer customer = new Customer();
		BeanUtils.copyProperties(customerDTO, customer);
		customer.setStatus(EnumUtils.getEnum(CustomerStatusEnum.class, customerDTO.getStatus().toUpperCase()));
		customer.setType(EnumUtils.getEnum(CustomerTypeEnum.class, customerDTO.getType().toUpperCase()));
		return customer;
	}
}
