package io.kamsan.secureinvoices.services;

import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.User;

public interface UserService {
	
	UserDTO createUser(User user);
	UserDTO getUserByEmail(String email);
	void sendVerificationCode(UserDTO userDTO);
	User getUser(String email);
	UserDTO verifyCode(String email, String code);

}
