package com.raphaelcollin.iteminventory.domain;

import com.raphaelcollin.iteminventory.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Nested
    @DisplayName("method: findByQuery(ItemQuery)")
    class FindByQueryMethod {
        private final ItemQuery itemQuery = ItemFactoryForTests.newItemQuery();
        private final Item item1 = ItemFactoryForTests.newItemDomain();
        private final Item item2 = ItemFactoryForTests.newItemDomain();

        @AfterEach
        void tearDown() {
            verify(itemRepository).findByQuery(itemQuery);
            verifyNoMoreInteractions(itemRepository);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying repository")
        void whenCalled_shouldForwardTheCallToTheUnderlyingRepository() {
            when(itemRepository.findByQuery(itemQuery)).thenReturn(Flux.just(item1, item2));

            final Flux<Item> items = itemService.findByQuery(itemQuery);

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNext(item1, item2)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Item item = ItemFactoryForTests.newItemDomain();

        @AfterEach
        void tearDown() {
            verify(itemRepository).findById(item.getId());
            verifyNoMoreInteractions(itemRepository);
        }

        @Test
        @DisplayName("when the item is found, then it should return the matching item")
        void whenItemIsFound_shouldReturnTheMatchingItem() {
            when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));

            final Mono<Item> mono = itemService.findById(item.getId());

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(item)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when the item is not found, then it should return an error")
        void whenItemIsNotFound_shouldReturnAnError() {
            when(itemRepository.findById(item.getId())).thenReturn(Mono.empty());

            final Mono<Item> mono = itemService.findById(item.getId());

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectError(EntityNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("method: save(Item)")
    class SaveMethod {
        private final Item item = ItemFactoryForTests.newItemDomain();

        @BeforeEach
        void setUp() {
            when(itemRepository.save(item)).thenReturn(Mono.empty());
        }

        @AfterEach
        void tearDown() {
            verify(itemRepository).save(item);
            verifyNoMoreInteractions(itemRepository);
        }

        @Test
        @DisplayName("when called, then it should forward the call to underlying item")
        void whenCalled_shouldForwardTheCallToUnderlyingItem() {
            StepVerifier.create(itemService.save(item))
                    .expectSubscription()
                    .expectNext(item)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        final String itemId = UUID.randomUUID().toString();

        @AfterEach
        void tearDown() {
            verify(itemRepository).deleteById(itemId);
            verifyNoMoreInteractions(itemRepository);
        }

        @Test
        @DisplayName("when the item is deleted successfully, then it should not return any error")
        void whenItemIsDeletedSuccessfully_shouldNotReturnAnyError() {
            when(itemRepository.deleteById(itemId)).thenReturn(Mono.just(true));

            Mono<Void> mono = itemService.deleteById(itemId);

            StepVerifier.create(mono)
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        @DisplayName("when the item is not deleted successfully, then it should return an error")
        void whenTheItemIsNotDeletedSuccessfully_shouldReturnAnError() {
            when(itemRepository.deleteById(itemId)).thenReturn(Mono.just(false));

            Mono<Void> mono = itemService.deleteById(itemId);

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectError(EntityNotFoundException.class)
                    .verify();
        }
    }

}