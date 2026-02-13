package com.fempa.dam.users.api.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDto(String token, Long userId, String name, String email, String role) {
}
