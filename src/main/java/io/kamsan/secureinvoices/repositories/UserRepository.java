package io.kamsan.secureinvoices.repositories;

import java.util.Collection;

import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.User;

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
	

}
