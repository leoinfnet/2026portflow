package br.com.infnet.infra;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RsaPrivateKeyLoader {
    public static RSAPrivateKey loadPrivateKey(String resourcePath) {
        try {
            InputStream inputStream = RsaPrivateKeyLoader.class
                    .getClassLoader()
                    .getResourceAsStream(resourcePath);

            if (inputStream == null) {
                throw new IllegalArgumentException("Chave privada não encontrada: " + resourcePath);
            }

            String pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            String privateKeyContent = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar chave privada RSA", e);
        }
    }
}
