package dev.matheuslf.desafio.inscritos.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    Middleware middleware;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        //User Login
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/confirm").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/resend-code").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/recover-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/auth/delete-my-account").permitAll()

                        //Admin Login
                        .requestMatchers(HttpMethod.POST, "/internal/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/internal/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/internal/confirm").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/internal/resend-code").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/internal/recover-password").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/internal/reset-password").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/internal/delete-my-account").hasRole("ADMIN")

                        //SWAGGER
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        //PROJECT
                        .requestMatchers(HttpMethod.POST, "/projects/addProject").hasAnyRole("ADMIN", "PM")
                        .requestMatchers(HttpMethod.PUT, "/projects/{id}").hasAnyRole("ADMIN", "PM")
                        .requestMatchers(HttpMethod.DELETE, "/projects/{id}").hasRole("ADMIN")

                        //TASK
                        .requestMatchers(HttpMethod.POST, "/tasks/addTask").hasAnyRole("ADMIN", "PM", "DEV")
                        .requestMatchers(HttpMethod.GET, "/tasks/{id}").hasAnyRole("ADMIN", "PM", "DEV")
                        .requestMatchers(HttpMethod.PUT, "/tasks/{id}/status").hasAnyRole("ADMIN", "PM", "DEV")
                        .requestMatchers(HttpMethod.DELETE, "/tasks/{id}").hasAnyRole("ADMIN", "PM")

                        //Manager Users
                        .requestMatchers(HttpMethod.GET, "/manager-users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/manager-users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/manager-users/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(middleware, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
