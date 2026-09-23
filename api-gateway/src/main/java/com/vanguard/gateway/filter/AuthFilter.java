package com.vanguard.gateway.filter;

import com.vanguard.gateway.config.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Routes that do NOT require a JWT
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    public AuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private boolean isSecured(ServerHttpRequest request) {
        return OPEN_API_ENDPOINTS.stream()
                .noneMatch(uri -> request.getURI().getPath().contains(uri));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isSecured(request)) {
            if (!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange, "Missing Authorization header");
            }

            String authHeader = request.getHeaders().getOrEmpty("Authorization").get(0);
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            if (!jwtUtil.isTokenValid(token)) {
                return onError(exchange, "Invalid or expired token");
            }

            // Forward user/role info downstream as headers
            String role = jwtUtil.extractClaims(token).get("role", String.class);
            String username = jwtUtil.extractClaims(token).getSubject();

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User", username)
                    .header("X-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        byte[] bytes = ("{\"error\": \"" + message + "\"}").getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
