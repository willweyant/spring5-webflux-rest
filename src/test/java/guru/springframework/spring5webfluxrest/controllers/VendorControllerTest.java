package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class VendorControllerTest {
    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController vendorController;

    @Before
    public void setUp() {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Tim").lastName("Brown").build(),
                        Vendor.builder().firstName("Jeff").lastName("Hostetler").build()));

        webTestClient.get().uri("/api/v1/vendors/")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void findById() {
        final Vendor vendor = Vendor.builder().firstName("Tim").lastName("Brown").build();
        given(vendorRepository.findById("someid"))
                .willReturn(Mono.just(vendor));

        webTestClient.get().uri("/api/v1/vendors/someid/")
                .exchange()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);
    }

    @Test
    public void create() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        final Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("Bob").lastName("Smith").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void update() {
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        final Mono<Vendor> vendorToUpdateMono = Mono.just(Vendor.builder().firstName("First").lastName("Last").build());

        webTestClient.put()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void patchWithFirstNameChange() {
        final Vendor foundVendor = Vendor.builder().id("someId").firstName("Bob").lastName("Smith").build();
        final Vendor patchedVendor = new Vendor();
        patchedVendor.setId(foundVendor.getId());
        patchedVendor.setFirstName("Bobby");
        patchedVendor.setLastName(foundVendor.getLastName());

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(foundVendor));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(patchedVendor));

        final Mono<Vendor> vendorToUpdateMono = Mono.just(patchedVendor);

        webTestClient.patch()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void patchWithLastNameChange() {
        final Vendor foundVendor = Vendor.builder().id("someId").firstName("Bob").lastName("Smith").build();
        final Vendor patchedVendor = new Vendor();
        patchedVendor.setId(foundVendor.getId());
        patchedVendor.setFirstName(foundVendor.getFirstName());
        patchedVendor.setLastName("Jones");

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(foundVendor));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(patchedVendor));

        final Mono<Vendor> vendorToUpdateMono = Mono.just(patchedVendor);

        webTestClient.patch()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void patchWithNoChanges() {
        final Vendor foundVendor = Vendor.builder().id("someId").firstName("Bob").lastName("Smith").build();

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(foundVendor));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(foundVendor));

        final Mono<Vendor> vendorToUpdateMono = Mono.just(foundVendor);

        webTestClient.patch()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository, never()).save(any());
    }
}