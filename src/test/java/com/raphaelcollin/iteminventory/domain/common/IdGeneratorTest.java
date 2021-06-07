package com.raphaelcollin.iteminventory.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    private final IdGenerator idGenerator = new IdGenerator();

    @Test
    @DisplayName("when called, then it should generate an uuid")
    void whenCalled_shouldGenerateAnUUID() {
        assertThat(idGenerator.newId())
                .hasSize(36);
    }
}