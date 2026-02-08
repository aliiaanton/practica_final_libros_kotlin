package com.fempa.dam.users.api.dto.request;

import lombok.Builder;

@Builder
public record UserRequestDto(String name, String email, String password) {
}
