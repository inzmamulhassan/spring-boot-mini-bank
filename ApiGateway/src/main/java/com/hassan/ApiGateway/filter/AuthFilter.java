package com.hassan.ApiGateway.filter;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    @Autowired
    private RestTemplate restTemplate;
    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Todo: Refactor the auth process. Currently it is not entirely secure and prone to attacks
        // Todo: Use a more secure way to validate the token
        return (exchange, chain) -> {
            if (RouteValidator.isSecured.test(exchange.getRequest())) {
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).getFirst();
                if (authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                ServerHttpRequest request;
                try {
                    // Validate token  from auth service
                    // Todo: Change the URL to the auth service URL
                    // Todo: Remove direct call to auth service
                    final String forObject = restTemplate.getForObject("http://localhost:9090/api/v1/auth/validate?token=" + authHeader, String.class);
                    request = exchange.getRequest().mutate().header("loggedInUser", forObject).build();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange.mutate().request(request).build());
            }
            return chain.filter(exchange);
        };
    }

    public static class Config{

    }
}
