package backend.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class TokenManager {
    private static final String SECRET_KEY = "uWhg3yB6i3Ajd/OzvN5WOW5qLRZHyHSIln9nEMZXEt0=";
    private static final long TOKEN_EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutos em milissegundos

    public static String generateToken(String username) {
        Date expirationDate = new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(username)
                .claim("username", username) // Adiciona informações personalizadas
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Token inválido
            return false;
        }
    }

    public static String decodeToken(String token) {
        try {
            return (String) Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody().get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
