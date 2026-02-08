package com.fempa.dam.users.api.security.jwt;

import com.fempa.dam.users.api.entity.user.UserEntity;
import com.fempa.dam.users.api.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String token = header.substring("Bearer ".length()).trim();

		try {
			final String email = this.jwtService.extractEmail(token);
			final Optional<UserEntity> userOpt = this.userRepository.findByEmail(email);

			if (userOpt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
				final UserEntity user = userOpt.get();
				final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		} catch (final Exception ignored) {
			// Token inválido -> no autenticamos
		}

		filterChain.doFilter(request, response);
	}

}
