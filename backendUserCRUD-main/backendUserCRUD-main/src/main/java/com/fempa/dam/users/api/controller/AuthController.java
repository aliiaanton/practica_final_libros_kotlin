package com.fempa.dam.users.api.controller;

import com.fempa.dam.users.api.dto.request.LoginRequestDto;
import com.fempa.dam.users.api.dto.request.UserRequestDto;
import com.fempa.dam.users.api.dto.response.AuthResponseDto;
import com.fempa.dam.users.api.dto.response.UserResponseDto;
import com.fempa.dam.users.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto) {
		return ResponseEntity.ok(this.userService.registerUser(userRequestDto));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
		return ResponseEntity.ok(this.userService.loginUser(loginRequestDto));
	}
}
