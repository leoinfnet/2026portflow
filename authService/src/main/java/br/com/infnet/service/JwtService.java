package br.com.infnet.service;

import br.com.infnet.dto.UsuarioAutenticadoResponse;
import br.com.infnet.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private static final String ISSUER = "portflow-auth-service";
    private static final long ACCESS_TOKEN_SECONDS = 60;
    private static final String SECRET = "MEU-SEGREDO-SECRETO-1230";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);
    public String gerarAccessToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant daqui30Segundos = agora.plusSeconds(ACCESS_TOKEN_SECONDS);
        return JWT.create()
                .withIssuer(ISSUER)
                .withClaim("nome", usuario.getNome())
                .withClaim("email", usuario.getEmail())
                .withClaim("roles", usuario.getRoles().stream().toList())
                .withSubject(usuario.getId().toString())
                .withIssuedAt(Date.from(agora))
                .withExpiresAt(Date.from(daqui30Segundos))
                .sign(algorithm);

    }
    public long getExpireTime(){
        return ACCESS_TOKEN_SECONDS;
    }

    public UsuarioAutenticadoResponse verificarToken(String token) {
        DecodedJWT decoded = validar(token);
        return new UsuarioAutenticadoResponse(
                decoded.getSubject(),
                decoded.getClaim("name").asString(),
                decoded.getClaim("email").asString(),
                decoded.getClaim("roles").asList(String.class)
        );

    }
    public DecodedJWT validar(String token){
        return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token);
    }
}
