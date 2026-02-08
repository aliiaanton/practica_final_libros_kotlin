package com.fempa.dam.users.api.service;

import com.fempa.dam.users.api.dto.request.LoginRequestDto;
import com.fempa.dam.users.api.dto.request.UpdateUserRequestDto;
import com.fempa.dam.users.api.dto.request.UserRequestDto;
import com.fempa.dam.users.api.dto.response.AuthResponseDto;
import com.fempa.dam.users.api.dto.response.UserResponseDto;
import com.fempa.dam.users.api.entity.user.UserEntity;
import com.fempa.dam.users.api.mapper.UserMapper;
import com.fempa.dam.users.api.repository.UserRepository;
import com.fempa.dam.users.api.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	public UserResponseDto registerUser(UserRequestDto userRequestDto) {
		if (this.userRepository.existsByEmail(userRequestDto.email()))
			throw new IllegalArgumentException("Email already in use");

		this.validateUserRequest(userRequestDto);

		final UserEntity userEntity = UserEntity.builder().name(userRequestDto.name()).email(userRequestDto.email())
				.password(this.passwordEncoder.encode(userRequestDto.password())).role("USER").build();

		this.userRepository.save(userEntity);
		return UserMapper.INSTANCE.toUserResponse(userEntity);
	}

	public AuthResponseDto loginUser(LoginRequestDto loginRequestDto) {
		final UserEntity userEntity = this.userRepository.findByEmail(loginRequestDto.email())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
		if (!this.passwordEncoder.matches(loginRequestDto.password(), userEntity.getPassword())) {
			throw new IllegalArgumentException("Invalid email or password");
		}
		final String token = this.jwtService.generateToken(userEntity.getId(), userEntity.getEmail(),
				userEntity.getRole());
		return new AuthResponseDto(token, userEntity.getId(), userEntity.getName(), userEntity.getEmail(),
				userEntity.getRole());
	}

	public List<UserResponseDto> getAllUsers() {
		return this.userRepository.findAll().stream().map(UserMapper.INSTANCE::toUserResponse).toList();
	}

	public UserResponseDto updateUser(Long userId, UpdateUserRequestDto userRequestDto) {
		if (!StringUtils.hasText(userRequestDto.name()) || userRequestDto.name().isBlank()) {
			throw new IllegalArgumentException("Name cannot be empty");
		}
		final UserEntity userEntity = this.userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		userEntity.setName(userRequestDto.name());
		this.userRepository.save(userEntity);

		return UserMapper.INSTANCE.toUserResponse(userEntity);
	}

	public void deleteUser(Long userId) {
		if (!this.userRepository.existsById(userId)) {
			throw new IllegalArgumentException("User not found");
		}
		this.userRepository.deleteById(userId);
	}

	private void validateUserRequest(UserRequestDto userRequestDto) {
		if (!StringUtils.hasText(userRequestDto.name())) {
			throw new IllegalArgumentException("Name cannot be empty");
		} else if (!StringUtils.hasText(userRequestDto.email()) || !userRequestDto.email().contains("@")) {
			throw new IllegalArgumentException("Invalid email format");
		} else if (!StringUtils.hasText(userRequestDto.password())) {
			throw new IllegalArgumentException("Password cannot be empty");
		}
	}

}
