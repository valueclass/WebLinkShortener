package web.links.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.server.WebFilter;
import web.links.auth.ApiRequestServerAuthenticationConverter;
import web.links.auth.ResponseProducingAuthenticationFailureHandler;
import web.links.auth.HttpStatusReturningAuthenticationSuccessHandler;
import web.links.utils.PasswordUtils;

import java.util.Map;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity security) {
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
                    .logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler())
                    .logoutUrl("/api/v1/users/logout")
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        final String defaultId = "argon2";
        final PasswordEncoder defaultEncoder = PasswordUtils.argon2Encoder();
        final Map<String, PasswordEncoder> encoders = Map.of(
                defaultId, defaultEncoder,
                "bcrypt", PasswordUtils.bcryptEncoder()
        );

        final DelegatingPasswordEncoder encoder = new DelegatingPasswordEncoder(defaultId, encoders);
        encoder.setDefaultPasswordEncoderForMatches(defaultEncoder);

        return encoder;
    }

    @Autowired
    private ReactiveUserDetailsService details;

    public WebFilter apiLoginFilter(ServerSecurityContextRepository repository) {
        final ReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(details);
        final AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);

        filter.setRequiresAuthenticationMatcher(new PathPatternParserServerWebExchangeMatcher("/api/v1/users/login"));
        filter.setAuthenticationFailureHandler(new ResponseProducingAuthenticationFailureHandler());
        filter.setAuthenticationSuccessHandler(new HttpStatusReturningAuthenticationSuccessHandler());
        filter.setSecurityContextRepository(repository);
        filter.setServerAuthenticationConverter(new ApiRequestServerAuthenticationConverter());

        return filter;
    }
}
