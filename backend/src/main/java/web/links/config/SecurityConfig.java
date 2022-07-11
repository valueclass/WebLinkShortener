package web.links.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain httpSecurity(ServerHttpSecurity security) {
        return security
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"))
                .authorizeExchange()
                    .pathMatchers("/api/v1/links/**").permitAll()
                    .anyExchange().authenticated()
                    .and()
                .csrf()
                    .disable()
                .httpBasic()
                    .disable()
                .build();
    }

    @Bean
    public SecurityWebFilterChain webHttpSecurity(ServerHttpSecurity security) {
        return security.authorizeExchange()
                    .anyExchange().permitAll()
                    .and()
                .build();
    }
}
