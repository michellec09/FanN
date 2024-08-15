package edu.tec.azuay.faan.config.security.filter;

import edu.tec.azuay.faan.exceptions.ObjectNotFoundException;
import edu.tec.azuay.faan.persistence.entity.User;
import edu.tec.azuay.faan.service.auth.JwtService;
import edu.tec.azuay.faan.service.interfaces.IUserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private final IUserService userService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		if(!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String jwt = authorizationHeader.split(" ")[1];

		try {

			String username = jwtService.extractUsername(jwt);

			User user = userService.findOneByUsername(username).orElseThrow(() -> new ObjectNotFoundException("User not found. Username: " + username));
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
			authToken.setDetails(new WebAuthenticationDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		} catch (ExpiredJwtException e) {
			SecurityContextHolder.clearContext();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Token JWT expirado\"}");
			return;
		}
		filterChain.doFilter(request, response);
	}

}
