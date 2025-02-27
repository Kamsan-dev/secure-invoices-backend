package io.kamsan.secureinvoices.dtomapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.kamsan.secureinvoices.dtos.InvoiceDTO;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.entities.invoices.Invoice;

public class UserDTOMapper {
	
	public static UserDTO fromUser(User user) {
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(user, userDTO);
		return userDTO;
	}
	
	public static UserDTO fromUser(User user, Role role) {
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(user, userDTO);
		userDTO.setRoleName(role.getName());
		userDTO.setPermissions(role.getPermission());
		return userDTO;
	}
	
	public static User fromUserDTO(UserDTO userDTO) {
		User user = new User();
		BeanUtils.copyProperties(userDTO, user);
		return user;
	}

}
