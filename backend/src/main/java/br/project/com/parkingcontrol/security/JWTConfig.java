package br.project.com.parkingcontrol.security;

import br.project.com.parkingcontrol.util.AuthenticationResponse;
import br.project.com.parkingcontrol.security.filter.JWTAuthenticate;
import br.project.com.parkingcontrol.security.filter.JWTValidate;
import br.project.com.parkingcontrol.util.TokenGenerator;
import br.project.com.parkingcontrol.domain.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class JWTConfig {
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;
    private AuthenticationResponse authenticationResponse;
    private final UserServiceImpl userServiceImpl;

    public JWTConfig(TokenGenerator tokenGenerator,
                     PasswordEncoder passwordEncoder,
                     AuthenticationConfiguration authenticationConfiguration,
                     UserServiceImpl userServiceImpl) {

        this.tokenGenerator = tokenGenerator;
        this.passwordEncoder = passwordEncoder;
        this.authenticationConfiguration = authenticationConfiguration;
        this.userServiceImpl = userServiceImpl;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceImpl).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeHttpRequests((authorize) -> {
                    try {
                        authorize
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/signup").permitAll()
                                .anyRequest().authenticated()
                                .and()
                                .addFilter(new JWTAuthenticate.Builder()
                                                .setTokenGenerate(tokenGenerator)
                                                .setAuthenticationManager(authenticationConfiguration.getAuthenticationManager())
                                                .setAuthenticationResponse(authenticationResponse)
                                                .build())
                                .addFilterAfter(new JWTValidate(),
                                                JWTAuthenticate.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("PATCH");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Requestor-Type"));
        corsConfig.setExposedHeaders(Arrays.asList("X-Get-Header"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}