package io.kamsan.secureinvoices.repositories;

import java.util.Collection;

import io.kamsan.secureinvoices.entities.Role;

public interface RoleRepository <T extends Role> {
	
	/* Basic CRUD Operations */
	T create(T data);
	Collection<T> list(int page, int pageSize);
	T get(Long id);
	T update(T data);
	Boolean delete(Long id);
	
	/* More Complex Operations */
	
	void addRoleToUser(Long userId, String roleName);
	Role getRoleByUserId(Long userId);
	Role getRoleByUserEmail(String email);
	void updateUserRole(Long userId, String roleName);

}