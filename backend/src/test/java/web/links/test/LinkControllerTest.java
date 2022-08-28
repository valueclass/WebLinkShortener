package web.links.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.links.controller.v1.LinkController;
import web.links.controller.v1.LinkControllerRouter;
import web.links.dto.LinkDto;
import web.links.exception.LinkAccessDeniedException;
import web.links.exception.LinkNotFoundException;
import web.links.service.LinkQueryOptions;
import web.links.service.LinkService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { LinkControllerRouter.class, LinkController.class })
@WebFluxTest(value = LinkController.class, excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
public class LinkControllerTest {
    @MockBean
    private LinkService service;

    @Autowired
    private WebTestClient client;

    @Test
    public void returnsLinkForGivenId() {
        final LinkDto link = LinkDto.fromModel(TestData.ALICE_PUBLIC_LINK);

        when(service.findLink("", TestData.ALICE_PUBLIC_LINK_ID)).thenReturn(Mono.just(link));

        client.get()
                .uri("/api/v1/links/" + TestData.ALICE_PUBLIC_LINK_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.id", TestData.ALICE_PUBLIC_LINK_ID).exists()
                    .jsonPath("$.name", TestData.ALICE_PUBLIC_LINK.name());
    }

    @Test
    public void returnsNotFoundWhenLinkNotFound() {
        when(service.findLink("", TestData.ALICE_PUBLIC_LINK_ID)).thenReturn(Mono.error(() -> new LinkNotFoundException("Link was not found")));

        client.get()
                .uri("/api/v1/links/" + TestData.ALICE_PUBLIC_LINK_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody();
    }

    @Test
    public void returnsMultipleLinks() {
        final List<LinkDto> links = List.of(
                LinkDto.fromModel(TestData.ALICE_PUBLIC_LINK),
                LinkDto.fromModel(TestData.ALICE_PRIVATE_LINK)
        );

        when(service.allLinks(anyString(), any(LinkQueryOptions.class))).thenReturn(Flux.fromIterable(links));

        client.get()
                .uri("/api/v1/links/")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkDto.class);
    }

    @Test
    public void returnsNotFoundWhenUserCannotAccessALink() {
        when(service.findLink(anyString(), anyString())).thenReturn(Mono.error(() -> new LinkAccessDeniedException("Cannot access this link")));

        client.get()
                .uri("/api/v1/links" + TestData.ALICE_PRIVATE_LINK_ID)
                .exchange()
                .expectStatus().isNotFound();
    }
}
