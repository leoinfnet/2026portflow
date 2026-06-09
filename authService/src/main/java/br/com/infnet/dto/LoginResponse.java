package br.com.infnet.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresAt ) {
}
