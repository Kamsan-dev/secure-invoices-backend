package io.kamsan.secureinvoices.services;

import java.util.Collection;

import io.kamsan.secureinvoices.entities.Role;

public interface RoleService {
	
	Role getRoleByUserId(Long userId);
	Collection<Role> getRoles();

}
