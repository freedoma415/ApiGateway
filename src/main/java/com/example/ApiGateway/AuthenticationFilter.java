package com.example.ApiGateway;

import java.security.Key;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    // 🚨 THIS MUST MATCH THE SECRET IN YOUR AUTH SERVICE EXACTLY
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        System.out.println("🛡️ [GATEWAY] Request incoming... checking for VIP pass.");

        // 1. Look for the "Authorization" header
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            System.out.println("🚨 [GATEWAY] BLOCKED: No Authorization Header found!");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // Kicks them out
        }

        // 2. Extract the header
        String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        
        // 3. Ensure it is a "Bearer" token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Removes the word "Bearer " to get just the token
            
            try {
                // 4. Cryptographically verify the token's signature and expiration
                Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
                    
                System.out.println("✅ [GATEWAY] Token verified! Letting request pass to backend...");
                
            } catch (Exception e) {
                System.out.println("🚨 [GATEWAY] BLOCKED: Token is forged or expired!");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            System.out.println("🚨 [GATEWAY] BLOCKED: Malformed token format!");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 5. If everything is good, let the request continue to the Product or Order Service
        return chain.filter(exchange);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public int getOrder() {
        return -1; // This guarantees this filter runs FIRST before anything else
    }
}