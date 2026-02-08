package com.fempa.dam.users.api.controller;

import com.fempa.dam.users.api.dto.request.UpdateUserRequestDto;
import com.fempa.dam.users.api.dto.response.UserResponseDto;
import com.fempa.dam.users.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<List<UserResponseDto>> getAllUsers() {
		return ResponseEntity.ok(this.userService.getAllUsers());
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
			@RequestBody UpdateUserRequestDto updateUserRequestDto) {
		return ResponseEntity.ok(this.userService.updateUser(id, updateUserRequestDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
		this.userService.deleteUser(id);
		return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
	}
}
