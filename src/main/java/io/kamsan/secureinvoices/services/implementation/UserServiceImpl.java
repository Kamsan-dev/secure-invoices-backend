package io.kamsan.secureinvoices.services.implementation;

import static io.kamsan.secureinvoices.dtomapper.UserDTOMapper.fromUser;
import static io.kamsan.secureinvoices.dtomapper.UserDTOMapper.fromUserDTO;

import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.repositories.RoleRepository;
import io.kamsan.secureinvoices.repositories.UserRepository;
import io.kamsan.secureinvoices.services.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository<User> userRepository;
	private final RoleRepository<Role> rolereRepository;

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
		return fromUser(user, rolereRepository.getRoleByUserId(user.getUserId()));
	}
}
