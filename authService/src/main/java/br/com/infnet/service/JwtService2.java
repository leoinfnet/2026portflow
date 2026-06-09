package br.com.infnet.service;

import br.com.infnet.infra.RsaPrivateKeyLoader;
import br.com.infnet.infra.RsaPublicKeyLoader;
import br.com.infnet.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
@Service
public class JwtService2 {
    private static final String ISSUER = "auth-service";
    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 3600;
    private static final long ACCESS_TOKEN_SECONDS = 900;

    private static final String PRIVATE_KEY_PATH = "keys/private_key_pkcs8.pem";
    private static final String PUBLIC_KEY_PATH = "keys/public_key.pem";

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public JwtService2() {
        this.publicKey = RsaPublicKeyLoader.loadPublicKey(PUBLIC_KEY_PATH);
        this.privateKey = RsaPrivateKeyLoader.loadPrivateKey(PRIVATE_KEY_PATH);
    }

    public String gerarAccessToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(ACCESS_TOKEN_SECONDS);

        return JWT.create()
                .withIssuer(ISSUER)
                .withKeyId("auth-token-1")
                .withSubject(usuario.getId().toString())
                .withClaim("name", usuario.getNome())
                .withClaim("email", usuario.getEmail())
                .withClaim("roles", usuario.getRoles().stream().toList())
                .withIssuedAt(Date.from(agora))
                .withExpiresAt(Date.from(expiracao))
                .sign(getSigningAlgorithm());
    }

    public DecodedJWT validarToken(String token) {
        JWTVerifier verifier = JWT.require(getValidationAlgorithm())
                .withIssuer(ISSUER)
                .build();

        return verifier.verify(token);
    }

    public String extrairUsuario(String token) {
        return validarToken(token).getSubject();
    }
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public boolean tokenValido(String token) {
        try {
            validarToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Algorithm getSigningAlgorithm() {
        return Algorithm.RSA256(publicKey, privateKey);
    }

    private Algorithm getValidationAlgorithm() {
        return Algorithm.RSA256(publicKey, null);
    }
}
