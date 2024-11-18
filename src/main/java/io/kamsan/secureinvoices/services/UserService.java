package io.kamsan.secureinvoices.services;

import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.form.UpdateUserForm;
import jakarta.validation.Valid;

public interface UserService {
	
	UserDTO createUser(User user);
	UserDTO getUserByEmail(String email);
	void sendVerificationCode(UserDTO userDTO);
	UserDTO verifyCode(String email, String code);
	void resetPassword(String email);
	UserDTO verifyPasswordKey(String key);
	void renewPassword(String key, String password, String confirmPassword);
	UserDTO verifyAccountKey(String key);
	UserDTO updateUserDetails(UpdateUserForm user);
	UserDTO getUserById(Long userId);
	void updatePassword(Long userId, String password, String newPassword, String confirmPassword);
	void verifyPassword(Long userId, String password);
	void updateUserRole(Long userId, String roleName);
	void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked);

}
