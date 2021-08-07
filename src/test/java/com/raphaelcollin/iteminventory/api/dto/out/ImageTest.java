package com.raphaelcollin.iteminventory.api.dto.out;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {

    @Nested
    @DisplayName("method: fromDomain(Image)")
    class FromDomainMethod {

        @Test
        @DisplayName("when called, then it should convert from domain to dto")
        void whenCalled_shouldConvertFromDomainToDto() {
            final String identifier = UUID.randomUUID().toString();
            final String preSignedUrl = UUID.randomUUID().toString();

            final var domain = com.raphaelcollin.iteminventory.domain.storage.Image
                    .builder()
                    .identifier(identifier)
                    .preSignedUrl(preSignedUrl)
                    .build();

            final Image expected = new Image(identifier, preSignedUrl);
            final Image actual = Image.fromDomain(domain);

            assertThat(actual).isEqualTo(expected);
        }
    }
}