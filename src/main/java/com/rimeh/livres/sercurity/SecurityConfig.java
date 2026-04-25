package com.rimeh.livres.sercurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	KeycloakRoleConverter  keycloakRoleConverter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
	    http
	        // CSRF désactivé (API REST)
	        .csrf(csrf -> csrf.disable())

	        // CORS configuration
	        .cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
	            @Override
	            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
	                CorsConfiguration cors = new CorsConfiguration();

	                cors.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
	                cors.setAllowedMethods(Collections.singletonList("*"));
	                cors.setAllowedHeaders(Collections.singletonList("*"));
	                cors.setExposedHeaders(Collections.singletonList("Authorization"));

	                return cors;
	            }
	        }))

	        // Session stateless (JWT)
	        .sessionManagement(session ->
	            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        )

	        // Autorisations
	        .authorizeHttpRequests( requests ->
	        requests.requestMatchers("/api/all/**").permitAll() //.hasAnyAuthority("ADMIN","USER")
	        .requestMatchers(HttpMethod.GET,"/api/getbyid/**").hasAnyAuthority("ADMIN","USER")
	        // .requestMatchers(HttpMethod.POST,"/api/addliv/**").hasAuthority("ADMIN")
	        .requestMatchers(HttpMethod.PUT,"/api/updateliv/**").hasAuthority("ADMIN")
	        .requestMatchers(HttpMethod.DELETE,"/api/delliv/**").hasAuthority("ADMIN")
	        .anyRequest().authenticated() )
	        //.oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()));
	        .oauth2ResourceServer(ors->ors.jwt(jwt-> 
	               jwt.jwtAuthenticationConverter(keycloakRoleConverter)));

	        

	    return http.build();
	}
}