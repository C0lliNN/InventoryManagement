package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateItem;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchItems;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateItem;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.Item;
import com.raphaelcollin.inventorymanagement.domain.ItemFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.ItemQuery;
import com.raphaelcollin.inventorymanagement.domain.ItemService;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.domain.exceptions.RequestValidationException;
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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemApiTest {

    @InjectMocks
    private ItemApi itemApi;

    @Mock
    private ItemService itemService;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private RequestValidator requestValidator;

    @Nested
    @DisplayName("method: findItems(SearchItems)")
    class FindItemsMethod {
        private final SearchItems searchItems = ItemFactoryForTests.newSearchItems();
        private final ItemQuery itemQuery = searchItems.toDomain();

        private final Item item1 = ItemFactoryForTests.newItemDomain();
        private final Item item2 = ItemFactoryForTests.newItemDomain();
        private final Item item3 = ItemFactoryForTests.newItemDomain();

        @BeforeEach
        void setUp() {
            when(itemService.findByQuery(itemQuery)).thenReturn(Flux.just(item1, item2, item3));
        }

        @AfterEach
        void tearDown() {
            verify(itemService).findByQuery(itemQuery);
            verifyNoMoreInteractions(itemService);

            verifyNoInteractions(idGenerator, requestValidator);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying service and convert the result to dto")
        void whenCalled_shouldForwardTheCallToTheUnderlyingServiceAndConvertTheResultDto() {
            StepVerifier.create(itemApi.findItems(searchItems))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Item.fromDomain(item1))
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Item.fromDomain(item2))
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Item.fromDomain(item3))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Item item1 = ItemFactoryForTests.newItemDomain();

        @BeforeEach
        void setUp() {
            when(itemService.findById(item1.getId())).thenReturn(Mono.just(item1));
        }

        @AfterEach
        void tearDown() {
            verify(itemService).findById(item1.getId());

            verifyNoMoreInteractions(itemService);
            verifyNoInteractions(requestValidator, idGenerator);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying service")
        void whenCalled_shouldForwardTheCallToTheUnderlyingServiceAndConvertTheResultDto() {
            StepVerifier.create(itemApi.findById(item1.getId()))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Item.fromDomain(item1))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: save(CreateItem)")
    class SaveMethod {
        private final CreateItem createItem = ItemFactoryForTests.newCreateItemDto();
        private Item item;

        @BeforeEach
        void setUp() {
            final String itemId = UUID.randomUUID().toString();
            this.item = createItem.toDomain(itemId);
        }

        @AfterEach
        void tearDown() {
            verify(requestValidator).validate(createItem);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying service")
        void whenCalled_shouldForwardTheCallToTheUnderlyingService() {
            when(requestValidator.validate(createItem)).thenReturn(Mono.just(createItem));
            when(itemService.save(item)).thenReturn(Mono.just(item));
            when(idGenerator.newId()).thenReturn(item.getId());

            StepVerifier.create(itemApi.save(createItem))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Item.fromDomain(item))
                    .verifyComplete();

            verify(itemService).save(item);
            verify(idGenerator).newId();
            verifyNoMoreInteractions(requestValidator, idGenerator, itemService);
        }

        @Test
        @DisplayName("when validation fails, then it should throw an error")
        void whenValidationFails_shouldThrowAnError() {
            when(requestValidator.validate(createItem)).thenReturn(Mono.error(new RequestValidationException(emptyList())));

            StepVerifier.create(itemApi.save(createItem))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(RequestValidationException.class)
                            .hasMessage("[]"))
                    .verify();

            verifyNoMoreInteractions(requestValidator);
            verifyNoInteractions(idGenerator, itemService);
        }
    }

    @Nested
    @DisplayName("method: updateById(String, UpdateItem)")
    class UpdateByIdMethod {
        private final UpdateItem updateItem = ItemFactoryForTests.newUpdateItemDto();
        private final Item existingItem = ItemFactoryForTests.newItemDomain();
        private final Item newItem = updateItem.toDomain(existingItem);

        @AfterEach
        void tearDown() {
            verify(requestValidator).validate(updateItem);
            verifyNoInteractions(idGenerator);
            verifyNoMoreInteractions(requestValidator);
        }

        @Test
        @DisplayName("when called, then it should forward the calls to the underlying services")
        void whenCalled_shouldForwardTheCallsToTheUnderlyingServices() {
            when(requestValidator.validate(updateItem)).thenReturn(Mono.just(updateItem));
            when(itemService.findById(existingItem.getId())).thenReturn(Mono.just(existingItem));
            when(itemService.save(newItem)).thenReturn(Mono.just(newItem));

            StepVerifier.create(itemApi.updateById(existingItem.getId(), updateItem))
                    .expectSubscription()
                    .verifyComplete();

            verify(itemService).findById(existingItem.getId());
            verify(itemService).save(newItem);
            verifyNoMoreInteractions(itemService);
        }

        @Test
        @DisplayName("when validator fails, then it should throw an error")
        void whenValidatorFails_shouldThrowAnError() {
            when(requestValidator.validate(updateItem)).thenReturn(Mono.error(new RequestValidationException(emptyList())));

            StepVerifier.create(itemApi.updateById(existingItem.getId(), updateItem))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(RequestValidationException.class).hasMessage("[]"))
                    .verify();

            verifyNoInteractions(itemService);
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        private final String itemId = UUID.randomUUID().toString();

        @BeforeEach
        void setUp() {
            when(itemService.deleteById(itemId)).thenReturn(Mono.empty());
        }

        @AfterEach
        void tearDown() {
            verify(itemService).deleteById(itemId);
            verifyNoInteractions(idGenerator, requestValidator);
            verifyNoMoreInteractions(itemService);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying service")
        void whenCalled_shouldForwardTheCallToTheUnderlyingService() {
            itemApi.deleteById(itemId).block();
        }
    }
}