package com.ebookstore.dto;

public record AuthResponse(String token, String email, String fullName) {
}
