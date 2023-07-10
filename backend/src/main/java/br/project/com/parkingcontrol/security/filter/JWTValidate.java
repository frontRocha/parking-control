package br.project.com.parkingcontrol.security.filter;


import br.project.com.parkingcontrol.util.BusinessException;
import br.project.com.parkingcontrol.util.ResponseData;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
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
import java.util.Date;

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

            validateHeaders(attribute);

            String token = attribute.replace(ATTRIBUTE_PREFIX, "");
            UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch(BusinessException err) {
            ResponseEntity<ResponseData> errorResponse = ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse("user not found"));

            writerResponseError(response, errorResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) throws BusinessException  {
        DecodedJWT decodedJWT = JWT.decode(token);
        Date expirationDate = decodedJWT.getExpiresAt();
        validateExpirationToken(expirationDate);

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

    private void writerResponseError(HttpServletResponse response, ResponseEntity<ResponseData> errorResponse) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(String.valueOf(errorResponse));
        response.getWriter().flush();
    }

    private void validateExpirationToken(Date expirationDate) throws BusinessException {
        if (expirationDate != null && expirationDate.before(new Date())) {
            throw new BusinessException("Token expired");
        }
    }

    private void validateHeaders(String atributo) throws BusinessException {
        if (atributo == null || !atributo.startsWith(ATTRIBUTE_PREFIX)) {
            throw new BusinessException("Token not found");
        }
    }
}