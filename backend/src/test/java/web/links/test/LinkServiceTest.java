package web.links.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import web.links.dto.LinkDto;
import web.links.exception.LinkNotFoundException;
import web.links.repository.LinkRepository;
import web.links.service.LinkQueryOptions;
import web.links.service.LinkService;
import web.links.service.R2dbcLinkService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = R2dbcLinkService.class)
public class LinkServiceTest {

    @MockBean
    private LinkRepository repository;

    @Autowired
    private LinkService service;

    @Test
    public void returnsAllEnabledPublicLinksWhenNotGivenOwnerId() {
        Flux<LinkDto> links = service.allLinks("", LinkQueryOptions.none());

        StepVerifier.create(links)
                .expectNext(
                        LinkDto.fromModel(TestData.ALICE_PUBLIC_LINK),
                        LinkDto.fromModel(TestData.ALICE_MODIFIED_LINK),
                        LinkDto.fromModel(TestData.BOB_PUBLIC_LINK),
                        LinkDto.fromModel(TestData.BOB_MODIFIED_LINK)
                )
                .verifyComplete();
    }

    @Test
    public void returnsAllEnabledPublicPlusPrivateAndDisabledWhenGivenOwnerId() {
        Flux<LinkDto> links = service.allLinks(TestData.ALICE_ID, LinkQueryOptions.none());

        StepVerifier.create(links)
                .expectNext(
                        LinkDto.fromModel(TestData.ALICE_PUBLIC_LINK),
                        LinkDto.fromModel(TestData.ALICE_MODIFIED_LINK),
                        LinkDto.fromModel(TestData.ALICE_PRIVATE_LINK),
                        LinkDto.fromModel(TestData.ALICE_DISABLED_LINK),
                        LinkDto.fromModel(TestData.BOB_PUBLIC_LINK),
                        LinkDto.fromModel(TestData.BOB_MODIFIED_LINK)
                )
                .verifyComplete();
    }

    @Test
    public void returnsPublicLinkWhenNotGivenOwnerId() {
        StepVerifier.create(service.findLink("", TestData.ALICE_PUBLIC_LINK_ID))
                .expectNext(LinkDto.fromModel(TestData.ALICE_PUBLIC_LINK))
                .verifyComplete();
    }

    @Test
    public void returnsErrorWhenAccessingPrivateLinkWhenNotGivenOwnerId() {
        StepVerifier.create(service.findLink("", TestData.ALICE_PRIVATE_LINK_ID))
                .expectError(LinkNotFoundException.class)
                .verify();
    }

    @Test
    public void returnsErrorWhenAccessingDisabledLinkWhenNotGivenOwnerId() {
        StepVerifier.create(service.findLink("", TestData.ALICE_DISABLED_LINK_ID))
                .expectError(LinkNotFoundException.class)
                .verify();
    }

    @Test
    public void returnsPrivateLinkWhenGivenOwnerId() {
        StepVerifier.create(service.findLink(TestData.ALICE_ID, TestData.ALICE_PRIVATE_LINK_ID))
                .expectNext(LinkDto.fromModel(TestData.ALICE_PRIVATE_LINK))
                .verifyComplete();
    }

    @Test
    public void returnsDisabledLinkWhenGivenOwnerId() {
        StepVerifier.create(service.findLink(TestData.ALICE_ID, TestData.ALICE_DISABLED_LINK_ID))
                .expectNext(LinkDto.fromModel(TestData.ALICE_DISABLED_LINK))
                .verifyComplete();
    }

    @Before
    public void setup() {
        when(repository.findAll(any(Example.class)))
                .thenReturn(Flux.just(
                        TestData.ALICE_PUBLIC_LINK, TestData.ALICE_MODIFIED_LINK, TestData.ALICE_PRIVATE_LINK, TestData.ALICE_DISABLED_LINK,
                        TestData.BOB_PUBLIC_LINK, TestData.BOB_MODIFIED_LINK, TestData.BOB_PRIVATE_LINK, TestData.BOB_DISABLED_LINK
                ));

        when(repository.findByLinkId(TestData.ALICE_PUBLIC_LINK_ID)).thenReturn(Mono.just(TestData.ALICE_PUBLIC_LINK));
        when(repository.findByLinkId(TestData.ALICE_MODIFIED_LINK_ID)).thenReturn(Mono.just(TestData.ALICE_MODIFIED_LINK));
        when(repository.findByLinkId(TestData.ALICE_PRIVATE_LINK_ID)).thenReturn(Mono.just(TestData.ALICE_PRIVATE_LINK));
        when(repository.findByLinkId(TestData.ALICE_DISABLED_LINK_ID)).thenReturn(Mono.just(TestData.ALICE_DISABLED_LINK));
        when(repository.findByLinkId(TestData.BOB_PUBLIC_LINK_ID)).thenReturn(Mono.just(TestData.BOB_PUBLIC_LINK));
        when(repository.findByLinkId(TestData.BOB_MODIFIED_LINK_ID)).thenReturn(Mono.just(TestData.BOB_MODIFIED_LINK));
        when(repository.findByLinkId(TestData.BOB_PRIVATE_LINK_ID)).thenReturn(Mono.just(TestData.BOB_PRIVATE_LINK));
        when(repository.findByLinkId(TestData.BOB_DISABLED_LINK_ID)).thenReturn(Mono.just(TestData.BOB_DISABLED_LINK));
    }
}
