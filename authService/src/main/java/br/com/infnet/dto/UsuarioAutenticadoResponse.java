package br.com.infnet.dto;

import java.util.List;

public record UsuarioAutenticadoResponse(
        String id,
        String nome,
        String email,
        List<String> roles) {
}
