package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    WebTestClient webTestClient;
    CategoryRepository categoryRepository;
    CategoryController categoryController;

    @Before
    public void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    public void list() {
        given(categoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Cat1").build(),
                        Category.builder().description("Cat2").build()));

        webTestClient.get().uri("/api/v1/categories/")
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);

    }

    @Test
    public void findById() {
        final Category category = Category.builder().description("Cat1").build();
        given(categoryRepository.findById("someId"))
                .willReturn(Mono.just(category));

        webTestClient.get()
                .uri("/api/v1/categories/someId/")
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(category);
    }

    @Test
    public void create() {
        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        final Mono<Category> categoryToSaveMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.post()
                .uri("/api/v1/categories")
                .body(categoryToSaveMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void update() {
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        final Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.put()
                .uri("/api/v1/categories/someId")
                .body(categoryToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void patchWithChanges() {
        final Category foundCategory = Category.builder().id("someId").description("description").build();
        final Category patchedCategory = new Category();
        patchedCategory.setId(foundCategory.getId());
        patchedCategory.setDescription("New Description");

        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(foundCategory));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(patchedCategory));

        final Mono<Category> categoryToUpdateMono = Mono.just(patchedCategory);

        webTestClient.patch()
                .uri("/api/v1/categories/someId")
                .body(categoryToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository).save(any());
    }

    @Test
    public void patchWithNoChanges() {
        final Category foundCategory = Category.builder().id("someId").description("description").build();

        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(foundCategory));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(foundCategory));

        final Mono<Category> categoryToUpdateMono = Mono.just(foundCategory);

        webTestClient.patch()
                .uri("/api/v1/categories/someId")
                .body(categoryToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository, never()).save(any());
    }
}