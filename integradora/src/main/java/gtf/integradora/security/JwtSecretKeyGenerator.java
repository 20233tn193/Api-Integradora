package gtf.integradora.security;

import java.util.Base64;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

public class JwtSecretKeyGenerator {

    public static void main(String[] args) {
        @SuppressWarnings("deprecation")
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        String endocedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Key generada: "+endocedKey);
    }
}