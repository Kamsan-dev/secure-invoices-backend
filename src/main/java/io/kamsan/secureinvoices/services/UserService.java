package io.kamsan.secureinvoices.services;

import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.User;

public interface UserService {
	
	UserDTO createUser(User user);
	UserDTO getUserByEmail(String email);
	void sendVerificationCode(UserDTO userDTO);
	UserDTO verifyCode(String email, String code);
	void resetPassword(String email);
	UserDTO verifyPasswordKey(String key);
	void renewPassword(String key, String password, String confirmPassword);
	UserDTO verifyAccountKey(String key);

}
