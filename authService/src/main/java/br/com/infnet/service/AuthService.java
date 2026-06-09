package br.com.infnet.service;

import br.com.infnet.dto.LoginRequest;
import br.com.infnet.dto.LoginResponse;
import br.com.infnet.model.Usuario;
import br.com.infnet.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request){
        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new RuntimeException("Credenciais invalidas"));

        if(!usuario.isAtivo()){
            throw new RuntimeException("Usuario Inativo");
        }
        if(!usuario.getSenhaHash().equals(request.senha())){
            throw new RuntimeException("Credenciais Invalidas");
        }
        String accessToken = jwtService.gerarAccessToken(usuario);
        return new LoginResponse(accessToken, "Bearer", jwtService.getExpireTime());
    }
}
