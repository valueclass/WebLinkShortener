package web.links.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import web.links.server.NotFoundExceptionHandler;

@Configuration
public class WebFluxConfig {

    @Bean
    public WebSessionManager webSessionManager() {
        final DefaultWebSessionManager manager = new DefaultWebSessionManager();
        final CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();

        resolver.setCookieName("SessionToken");

        manager.setSessionIdResolver(resolver);

        return manager;
    }

    @Bean
    @Order(-2)
    public NotFoundExceptionHandler notFoundExceptionHandler(final ApplicationContext context) {
        return new NotFoundExceptionHandler();
    }
}
