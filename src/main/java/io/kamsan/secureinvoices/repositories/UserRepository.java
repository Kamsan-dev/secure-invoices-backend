package io.kamsan.secureinvoices.repositories;

import java.util.Collection;

import io.kamsan.secureinvoices.entities.User;

public interface UserRepository<T extends User> {
	
	/* Basic CRUD Operations */
	T create(T data);
	Collection<T> list(int page, int pageSize);
	T get(Long id);
	T update(T data);
	Boolean delete(Long id);
	
	/* More Complex Operations */

}
