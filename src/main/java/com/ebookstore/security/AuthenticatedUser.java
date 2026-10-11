package com.ebookstore.security;

/** Principal placed in the SecurityContext after successful JWT validation. */
public record AuthenticatedUser(Long userId, String email) {
}
