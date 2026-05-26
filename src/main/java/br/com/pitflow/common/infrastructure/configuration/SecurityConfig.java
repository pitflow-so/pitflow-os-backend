package br.com.pitflow.common.infrastructure.configuration;

import br.com.pitflow.common.infrastructure.security.SecurityFilter;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.core.userdetails.User.builder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // CSRF desativado, pois será utilizado JWT (Stateless)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // Actuator para health check do Kubernetes
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/metrics").permitAll()

                        // Endpoints de Autenticação
                        .requestMatchers("/registry/auth/**").permitAll()

                        //Cadastro do mecânico
                        .requestMatchers(POST, "/registry/mechanics").permitAll()

                        // Hooks
                        .requestMatchers(PATCH, "/external/events/service-orders/**").permitAll()
                        .requestMatchers(GET, "/external/events/service-orders/decision").permitAll()

                        //Cadastro veículo
                        //.requestMatchers(POST, "/registry/vehicles").permitAll()
                        //.requestMatchers(GET, "/registry/vehicles/plate/{plate}").permitAll()

                        //Cadastro do cliente
                        .requestMatchers(POST,"/registry/customers").permitAll()
                        //.requestMatchers(GET, "/registry/customers/document/{document}").permitAll()

                        //Cria a OS
                        //.requestMatchers(POST, "/operation/service-orders").permitAll()
                        //.requestMatchers(PATCH, "/operation/service-orders/{id}/approve").permitAll()
                        //.requestMatchers(PATCH, "/operation/service-orders/{id}/cancel").permitAll() //TODO: Rever depois
                        //.requestMatchers(GET, "/operation/service-orders/{id}").permitAll()
                        //.requestMatchers(GET, "/operation/service-orders/status/{id}").permitAll()
                        //.requestMatchers(GET, "/operation/service-orders/{id}/duration").permitAll()
                        //.requestMatchers(PATCH, "/operation/service-orders/v2/{id}/budget-decision").permitAll()

                        // Endpoints do Swagger UI e API Docs
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        //TODO: removed it, used to test get API gateway url
                        .requestMatchers(GET,"/external/events/service-orders/api/url").permitAll()

                        //.requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                //.headers(headers -> headers.frameOptions(frame -> frame.disable())) // Para o console H2 funcionar
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(MechanicGateway repository) {
        return username -> {
            var mechanic = repository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Mechanic not found: " + username));

            return builder()
                    .username(mechanic.getUsername())
                    .password(mechanic.getPassword())
                    .authorities(mechanic.getRole())
                    .build();
        };
    }

}