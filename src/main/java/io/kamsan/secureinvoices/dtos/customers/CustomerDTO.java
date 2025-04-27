package io.kamsan.secureinvoices.dtos.customers;

import java.time.LocalDateTime;

import io.kamsan.secureinvoices.enums.customers.CustomerStatusEnum;
import io.kamsan.secureinvoices.enums.customers.CustomerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

	private Long customerId;
	private String name;
	private String email;
	private String type;
	private String status;
	private String address;
	private String phone;
	private String imageUrl;
	private LocalDateTime createdAt;
}
