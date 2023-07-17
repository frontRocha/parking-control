package br.project.com.parkingcontrol.security.filter;

import br.project.com.parkingcontrol.util.BusinessException;
import br.project.com.parkingcontrol.util.ResponseData;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

public class JWTValidate extends OncePerRequestFilter {

    private static final String HEADER_ATTRIBUTE = "Authorization";
    private static final String ATTRIBUTE_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String attribute = request.getHeader(HEADER_ATTRIBUTE);
            if (attribute == null || !attribute.startsWith(ATTRIBUTE_PREFIX)) {
                chain.doFilter(request, response);
                return;
            }

            String token = attribute.replace(ATTRIBUTE_PREFIX, "");
            validateToken(token);

            UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch(BusinessException err) {
            ResponseEntity<ResponseData> errorResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));

            writerResponseError(response, errorResponse.getBody());
            return;
        }

        chain.doFilter(request, response);
    }

    private void validateHeaders(String attribute) throws BusinessException {
        if (attribute == null || !attribute.startsWith(ATTRIBUTE_PREFIX)) {
            throw new BusinessException("token not found");
        }
    }

    private DecodedJWT validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(JWTAuthenticate.TOKEN_PASSWORD))
                    .build()
                    .verify(token);
        } catch (Exception ex) {
            throw new BusinessException("invalid token");
        }
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) throws BusinessException  {
        DecodedJWT decodedJWT = JWT.decode(token);

        String user = decodedJWT.getSubject();

        verifyUser(user);

        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }

    private String verifyUser(String user) {
        if (user == null) {
            return null;
        }

        return user;
    }

    private void writerResponseError(HttpServletResponse response, ResponseData errorResponse) throws IOException {
        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}