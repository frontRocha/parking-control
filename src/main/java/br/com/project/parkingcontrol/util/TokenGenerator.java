package br.com.project.parkingcontrol.util;

@Component
public class TokenGenerator {

    public String generateTokenAuthentication(String login, Integer id) {
        return JWT.create()
                .withSubject(login)
                .withClaim("id", id)
                .withExpiresAt(new Date(System.currentTimeMillis() + JWTAuthenticate.TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC512(JWTAuthenticate.TOKEN_PASSWORD));
    }

    public static Integer extractUserIdFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JWTAuthenticate.TOKEN_PASSWORD))
                .build()
                .verify(token);

        String user = decodedJWT.getSubject();
        verifyUserEnull(user);

        return decodedJWT.getClaim("id").asInt();
    }

    public String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        };
        return null;
    }

    public static Object verifyUserEnull(String user) {
        if (user == null) {
            return null;
        }

        return user;
    }
}
