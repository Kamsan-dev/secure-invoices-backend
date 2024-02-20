package io.kamsan.secureinvoices.repositories.implementation;

import static io.kamsan.secureinvoices.enums.RoleType.ROLE_USER;
import static io.kamsan.secureinvoices.query.RoleQuery.*;

import java.util.Collection;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.repositories.RoleRepository;
import io.kamsan.secureinvoices.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role>{
	
	private final NamedParameterJdbcTemplate jdbc;

	@Override
	public Role create(Role data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Role> list(int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role update(Role data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRoleToUser(Long userId, String roleName) {
		log.info("Adding role {} to user id : {}", roleName, userId);
		try {
			Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("roleName", roleName), new RoleRowMapper());
			System.out.println(role.toString());
			jdbc.update(INSERT_ROLE_TO_USER_QUERY, Map.of("userId", userId, "roleId", role.getRoleId()));
		} catch (EmptyResultDataAccessException exception) {
			throw new ApiException("No role found by name : " + ROLE_USER.name());
		} catch (Exception exception) {
			throw new ApiException("An error occured inside addRoleToUser, please try again ");
		}
	}

	@Override
	public Role getRoleByUserId(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role getRoleByUserEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserRole(Long userId, String roleName) {
		// TODO Auto-generated method stub
		
	}

}
