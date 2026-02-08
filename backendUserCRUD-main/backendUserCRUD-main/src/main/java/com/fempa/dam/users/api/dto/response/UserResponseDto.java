package com.fempa.dam.users.api.dto.response;

import lombok.Builder;

@Builder
public record UserResponseDto(Long id, String name, String email, String role) {
}
