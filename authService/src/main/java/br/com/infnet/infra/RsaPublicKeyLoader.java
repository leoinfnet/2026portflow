package br.com.infnet.infra;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaPublicKeyLoader {
    public static RSAPublicKey loadPublicKey(String resourcePath) {
        try {
            InputStream inputStream = RsaPublicKeyLoader.class
                    .getClassLoader()
                    .getResourceAsStream(resourcePath);

            if (inputStream == null) {
                throw new IllegalArgumentException("Chave pública não encontrada: " + resourcePath);
            }

            String pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            String publicKeyContent = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decodedKey = Base64.getDecoder().decode(publicKeyContent);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPublicKey) keyFactory.generatePublic(keySpec);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar chave pública RSA", e);
        }
    }
}
