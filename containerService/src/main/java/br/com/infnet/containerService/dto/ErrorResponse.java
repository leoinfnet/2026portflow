package br.com.infnet.containerService.dto;

public record ErrorResponse(
        String code,
        String message
) {
}
