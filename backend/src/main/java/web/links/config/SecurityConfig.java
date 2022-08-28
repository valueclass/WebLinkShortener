package web.links.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.server.WebFilter;
import web.links.auth.*;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain apiHttpSecurity(final ServerHttpSecurity security) {
        final ServerSecurityContextRepository repository = new WebSessionServerSecurityContextRepository();

        return security
                .securityContextRepository(repository)
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"))
                .authorizeExchange()
                    .pathMatchers(HttpMethod.GET, "/api/v1/links/**").permitAll()
                    .anyExchange().authenticated()
                    .and()
                .addFilterAt(apiLoginFilter(repository), SecurityWebFiltersOrder.AUTHENTICATION)
                .logout()
                    .logoutHandler(new DelegatingServerLogoutHandler(new WebSessionServerLogoutHandler(), new SecurityContextServerLogoutHandler()))
                    .logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler())
                    .logoutUrl("/api/v1/users/logout")
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(new ErrorServerAuthenticationEndpoint())
                    .and()
                .httpBasic()
                    .disable()
                .formLogin()
                    .disable()
                .csrf()
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

    @Autowired
    private ReactiveUserDetailsService details;

    public WebFilter apiLoginFilter(ServerSecurityContextRepository repository) {
        final ReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(details);
        final AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);

        filter.setRequiresAuthenticationMatcher(new PathPatternParserServerWebExchangeMatcher("/api/v1/users/login", HttpMethod.POST));
        filter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(new ErrorServerAuthenticationEndpoint()));
        filter.setAuthenticationSuccessHandler(new ResponseServerAuthenticationSuccessHandler());
        filter.setSecurityContextRepository(repository);
        filter.setServerAuthenticationConverter(new ApiRequestServerAuthenticationConverter());

        return filter;
    }
}
