package br.project.com.parkingcontrol.security.filter;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.data.UserDetail;
import br.project.com.parkingcontrol.util.authenticationResponse.AuthenticationResponse;
import br.project.com.parkingcontrol.util.errorResponse.ErrorResponse;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.util.TokenGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JWTAuthenticate extends UsernamePasswordAuthenticationFilter {
    private final TokenGenerator tokenGenerator;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationResponse authenticationResponse;

    public static final int TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;
    public static final String TOKEN_PASSWORD = "463408a1-54c9-4307-bb1c-6cced559f5a7";
    private static Map<String, Object> userData = new HashMap<>();

    private JWTAuthenticate(TokenGenerator tokenGenerator,
                            AuthenticationManager authenticationManager,
                            AuthenticationResponse authenticationResponse) {
        this.tokenGenerator = tokenGenerator;
        this.authenticationManager = authenticationManager;
        this.authenticationResponse = authenticationResponse;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper()
                    .readValue(request.getInputStream(), User.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getLogin(),
                    user.getPassword(),
                    new ArrayList<>()
            ));

        } catch (IOException e) {
            throw new RuntimeException("Falha ao autenticar usuario", e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        ErrorResponse errorResponse = BusinessException.setErrorResponse("user not default", HttpStatus.NOT_FOUND.value());

        writeJsonResponse(response, errorResponse);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        UserDetail user = (UserDetail) authResult.getPrincipal();

        String token = tokenGenerator.generateTokenAuthentication(user.getUsername(), user.getId());

        defineUserLoginInUserData(user.getUsername());
        defineUserIdInUserData(user.getId());
        AuthenticationResponse createAuthenticationResponse = createAuthenticationResponse(token);

        dataResponse(response, createAuthenticationResponse);
    }

    private AuthenticationResponse createAuthenticationResponse(String token) {
        return new AuthenticationResponse.Builder()
                .setToken(token)
                .setData(userData)
                .build();
    }

    private void defineUserLoginInUserData(String email) {
        userData.put("email", email);
    }

    private void defineUserIdInUserData(Integer id) {
        userData.put("id", id);
    }

    private void writeJsonResponse(HttpServletResponse response, Object responseObject) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.OK.value());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), responseObject);
    }


    private IOException dataResponse(HttpServletResponse response,
                                     AuthenticationResponse createAuthenticationResponse) {
        try {
            String jsonResponse = new ObjectMapper().writeValueAsString(createAuthenticationResponse);

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

            return null;
        } catch(IOException err) {
            return err;
        }
    }

    public static class Builder {
        private TokenGenerator tokenGenerator;
        private AuthenticationManager authenticationManager;
        private AuthenticationResponse authenticationResponse;

        public Builder() {
            this.tokenGenerator = null;
            this.authenticationManager = null;
            this.authenticationResponse = null;
        }

        public Builder setTokenGenerate(TokenGenerator tokenGenerator) {
            this.tokenGenerator = tokenGenerator;
            return this;
        }

        public Builder setAuthenticationManager(AuthenticationManager authenticationManager) {
            this.authenticationManager = authenticationManager;
            return this;
        }

        public Builder setAuthenticationResponse(AuthenticationResponse authenticationResponse) {
            this.authenticationResponse = authenticationResponse;
            return this;
        }

        public JWTAuthenticate build() {
            return new JWTAuthenticate(tokenGenerator, authenticationManager, authenticationResponse);
        }
    }
}
