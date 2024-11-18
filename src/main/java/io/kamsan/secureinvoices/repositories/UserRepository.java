package io.kamsan.secureinvoices.repositories;

import java.util.Collection;

import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.form.UpdateUserForm;

public interface UserRepository<T extends User> {
	
	/* Basic CRUD Operations */
	T create(T data);
	Collection<T> list(int page, int pageSize);
	T get(Long id);
	T update(T data);
	Boolean delete(Long id);
	T getUserByEmail(String email);
	
	/* More Complex Operations */
	
	void sendVerificationCode(User user);
	T verifyCode(String email, String code);
	void resetPassword(String email);
	User verifyPasswordKey(String key);
	void renewPassword(String key, String password, String confirmPassword);
	User verifyAccountKey(String key);
	User updateUserDetails(UpdateUserForm user);
	void udpatePassword(Long userId, String password, String newPassword, String confirmPassword);
	void verifyPassword(Long userId, String password);
	void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked);
	

}
