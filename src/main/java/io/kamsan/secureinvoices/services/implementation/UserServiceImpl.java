package io.kamsan.secureinvoices.services.implementation;

import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.dtomapper.UserDTOMapper;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.User;
import io.kamsan.secureinvoices.repositories.UserRepository;
import io.kamsan.secureinvoices.services.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	private final UserRepository<User> userRepository;

	@Override
	public UserDTO createUser(User user) {
		return UserDTOMapper.fromUser(userRepository.create(user));
	}

	@Override
	public UserDTO getUserByEmail(String email) {
		return UserDTOMapper.fromUser(userRepository.getUserByEmail(email));
	}

}
