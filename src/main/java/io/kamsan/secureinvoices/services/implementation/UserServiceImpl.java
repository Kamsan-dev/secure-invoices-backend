package io.kamsan.secureinvoices.services.implementation;

import static io.kamsan.secureinvoices.dtomapper.UserDTOMapper.fromUser;
import static io.kamsan.secureinvoices.dtomapper.UserDTOMapper.fromUserDTO;

import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.form.UpdateUserForm;
import io.kamsan.secureinvoices.repositories.RoleRepository;
import io.kamsan.secureinvoices.repositories.UserRepository;
import io.kamsan.secureinvoices.services.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository<User> userRepository;
	private final RoleRepository<Role> roleRepository;

	@Override
	public UserDTO createUser(User user) {
		return mapToUserDTO(userRepository.create(user));
	}

	@Override
	public UserDTO getUserByEmail(String email) {
		return mapToUserDTO(userRepository.getUserByEmail(email));
	}

	@Override
	public void sendVerificationCode(UserDTO userDTO) {
		userRepository.sendVerificationCode(fromUserDTO(userDTO));

	}

	@Override
	public UserDTO verifyCode(String email, String code) {
		return mapToUserDTO(userRepository.verifyCode(email, code));
	}

	private UserDTO mapToUserDTO(User user) {
		return fromUser(user, roleRepository.getRoleByUserId(user.getUserId()));
	}

	@Override
	public void resetPassword(String email) {
		userRepository.resetPassword(email);

	}

	@Override
	public UserDTO verifyPasswordKey(String key) {
		return mapToUserDTO(userRepository.verifyPasswordKey(key));
	}

	@Override
	public void renewPassword(String key, String password, String confirmPassword) {
		userRepository.renewPassword(key, password, confirmPassword);
	}

	@Override
	public UserDTO verifyAccountKey(String key) {
		return mapToUserDTO(userRepository.verifyAccountKey(key));
	}

	@Override
	public UserDTO updateUserDetails(UpdateUserForm user) {
		return mapToUserDTO(userRepository.updateUserDetails(user));

	}

	@Override
	public UserDTO getUserById(Long userId) {
		return mapToUserDTO(userRepository.get(userId));
	}

	@Override
	public void updatePassword(Long userId, String password, String newPassword, String confirmPassword) {
		userRepository.udpatePassword(userId, password, newPassword, confirmPassword);
		
	}

	@Override
	public void verifyPassword(Long userId, String password) {
		userRepository.verifyPassword(userId, password);
		
	}

	@Override
	public void updateUserRole(Long userId, String roleName) {
		roleRepository.updateUserRole(userId, roleName);
		
	}

	@Override
	public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
		userRepository.updateAccountSettings(userId, enabled, notLocked);
		
	}
}
