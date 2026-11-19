package org.cokeke.policysim.common;

import java.io.IOException;
import java.util.List;

import org.cokeke.policysim.components.ServerStatus;
import org.cokeke.policysim.exceptions.InvalidTokenException;
import org.cokeke.policysim.exceptions.InvalidRequestException;
import org.cokeke.policysim.exceptions.TokenExpiredException;
import org.cokeke.policysim.exceptions.UnauthorizedRequestException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.services.TokenService;
import org.springframework.http.HttpMethod;
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
    private final ServerStatus status;

    public TokenAuthenticationFilter(TokenService tokenService, ServerStatus status) {
        this.tokenService = tokenService;
        this.status = status;
    }
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();
        String method = request.getMethod();

        if (!status.isOnline()) {
            if (isOfflinePublicEndpoint(path, method)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (path.equals("/api/server/status") && HttpMethod.POST.matches(method)) {
                if (!setAuthContext(request, response)) {
                    return;
                }

                filterChain.doFilter(request, response);
                return;
            }

            throw new InvalidRequestException("Server is currently down");
        }

        if (isOnlinePublicEndpoint(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!setAuthContext(request, response)) return;
        filterChain.doFilter(request, response);
    }

    private boolean isOfflinePublicEndpoint(String path, String method) {
        if (HttpMethod.GET.matches(method)) {
            return path.equals("/api/server/status")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui/")
                || path.equals("/swagger-ui.html");
        }

        if (HttpMethod.POST.matches(method)) {
            return path.equals("/api/users/login");
        }

        return false;
    }

    private boolean setAuthContext(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        try {
            String tokenString = extractToken(request);
            User user = tokenService.getAuthenticatedUser(tokenString);

            var authentication =
                new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of()
                );

            SecurityContext context =
                SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            return true;

        } catch (
            InvalidTokenException
            | TokenExpiredException
            | UserNotFoundException
            | UnauthorizedRequestException ex
        ) {
            SecurityContextHolder.clearContext();

            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid or expired token"
            );

            return false;
        }
    }

    private boolean isOnlinePublicEndpoint(String path, String method) {
        if (HttpMethod.POST.matches(method)) {
            return path.equals("/api/users")
                || path.equals("/api/users/me")
                || path.equals("/api/users/login")
                || path.equals("/api/users/refresh");
        }

        if (HttpMethod.GET.matches(method)) {
            return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui/")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/webjars/")
                || path.equals("/api/usernames")
                || path.startsWith("/h2-console/")
                || path.equals("/api/server/status");
        }

        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();

            if (!token.isBlank()) {
                return token;
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    String token = cookie.getValue();

                    if (token != null && !token.isBlank()) {
                        return token;
                    }
                }
            }
        }

        throw new InvalidTokenException(
            "Invalid authentication token"
        );
    }
}