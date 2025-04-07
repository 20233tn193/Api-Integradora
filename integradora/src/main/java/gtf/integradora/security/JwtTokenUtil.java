package gtf.integradora.security;

import java.util.Date;
import java.util.List;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

    private String secretKey = "SLdMxVwuOdWmnCCrXunCP/k6NmCrRmsEGgf7wZVNRqs7LTOaj2bK+0Ek2tVeeaT87d1lr+iVLFtMh2OiKAwDqA==";

    // Generar el token JWT
    @SuppressWarnings("deprecation")
    public String generarToken(String username, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes()); // Convertir la clave secreta en un SecretKey

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día de expiración
                .signWith(key, SignatureAlgorithm.HS256)  // Firmamos el token con la clave secreta y el algoritmo HS256
                .compact();
    }

    // Obtener los claims desde el token
    @SuppressWarnings("deprecation")
    public Claims obtenerClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes()); // Convertir la clave secreta en un SecretKey

        JwtParser parser = Jwts.parser()  // Usamos parserBuilder() para construir el JwtParser
                .verifyWith(key)  // Configuramos la clave secreta para verificar la firma
                .build();

        return parser.parseClaimsJws(token).getBody(); // Obtener los claims del token
    }

    // Obtener el usuario del token
    public String obtenerUsuario(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Obtener los roles del token
    @SuppressWarnings("unchecked")
    public List<String> obtenerRoles(String token) {
        return (List<String>) obtenerClaims(token).get("roles");
    }

    // Validar si el token sigue siendo válido
    public boolean esTokenValido(String token) {
        return !obtenerClaims(token).getExpiration().before(new Date());
    }
}