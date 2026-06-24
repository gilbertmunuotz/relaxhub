package com.relaxhub.backend.security;

public record AuthenticatedUser(Long userId, String email) {
}
