package io.kamsan.secureinvoices.services;

import io.kamsan.secureinvoices.entities.Role;

public interface RoleService {
	
	Role getRoleByUserId(Long userId);

}
