package web.links.utils;

import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.List;

public class HandlerStrategiesResponseContext implements ServerResponse.Context {
    private final HandlerStrategies strategies;

    public HandlerStrategiesResponseContext(final HandlerStrategies strategies) {
        this.strategies = strategies;
    }

    @Override
    public List<HttpMessageWriter<?>> messageWriters() {
        return strategies.messageWriters();
    }

    @Override
    public List<ViewResolver> viewResolvers() {
        return strategies.viewResolvers();
    }
}
