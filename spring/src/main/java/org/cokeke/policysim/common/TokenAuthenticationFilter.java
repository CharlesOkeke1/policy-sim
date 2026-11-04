
package org.cokeke.policysim.common;

import java.io.IOException;
import java.util.List;

import org.cokeke.policysim.exceptions.InvalidTokenException;
import org.cokeke.policysim.exceptions.TokenExpiredException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.services.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    public TokenAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String tokenString = null;

        // Only use this for game session init request. This makes life easier for me.
        String authHeader = request.getHeader("Authorization");

        if (path.equals("/api/games/session")) {
            System.out.println(path);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            tokenString = authHeader.substring(7).trim();
            System.out.println("Session user token => " + tokenString);
        } else {
            if (path.equals("/api/users/login")
                || path.equals("/api/users")
                || path.equals("/api/users/refresh")
                || path.equals("/api/users/me")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/webjars/")
                || path.equals("/api/usernames")
                || path.startsWith("/h2-console/")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Default method for authorization
            Cookie[] cookies = request.getCookies();
            if (cookies == null || cookies.length == 0) {
                filterChain.doFilter(request, response);
                return;
            }

            
            for (Cookie c : cookies) {
                if ("auth_token".equals(c.getName())) {
                    tokenString = c.getValue();
                    break;
                }
            }

            if (tokenString == null || tokenString.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            User user = tokenService.getAuthenticatedUser(tokenString);
            var authentication = 
                new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of()
                );

            SecurityContext context = SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

        } catch (InvalidTokenException
                | TokenExpiredException
                | UserNotFoundException ex) {

            SecurityContextHolder.clearContext();

            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid or expired token"
            );
            return;
        }
        filterChain.doFilter(request, response);
    }
}
