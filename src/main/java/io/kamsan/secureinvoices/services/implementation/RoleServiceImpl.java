package io.kamsan.secureinvoices.services.implementation;

import java.util.Collection;

import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.repositories.RoleRepository;
import io.kamsan.secureinvoices.services.RoleService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

	private final RoleRepository<Role> roleRepository;

	@Override
	public Role getRoleByUserId(Long userId) {
		return roleRepository.getRoleByUserId(userId);
	}

	@Override
	public Collection<Role> getRoles() {
		return roleRepository.list();
	}

}
