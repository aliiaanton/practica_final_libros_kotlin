package com.fempa.dam.users.api.security;

import com.fempa.dam.users.api.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

	private static final String ADMIN_ROLE = "ADMIN";
	private static final String AUTH_API_PATH = "/api/auth/**";
	private static final String ADMIN_API_PATH = "/api/admin/**";
	private static final String H2_CONSOLE_PATH = "/h2/**";
	private static final String ASTERISK = "*";
	private static final String ASTERISK_PATTERN = "/**";

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtAuthFilter jwtAuthFilter) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable).cors(cors -> {
			cors.configurationSource(request -> {
				final var corsConfiguration = new CorsConfiguration();
				corsConfiguration.setAllowedOrigins(List.of(ASTERISK));
				corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
				corsConfiguration.setAllowedHeaders(List.of(ASTERISK));
				return corsConfiguration;
			});
		}).sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, ASTERISK_PATTERN).permitAll()
						.requestMatchers(AUTH_API_PATH, H2_CONSOLE_PATH).permitAll().requestMatchers(ADMIN_API_PATH)
						.hasRole(ADMIN_ROLE).anyRequest().authenticated())
				.headers(headerConfig -> headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
		httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}

}
