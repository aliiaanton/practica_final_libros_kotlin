package com.fempa.dam.users.api.dto.request;

import lombok.Builder;

@Builder
public record UpdateUserRequestDto(String name) {
}
