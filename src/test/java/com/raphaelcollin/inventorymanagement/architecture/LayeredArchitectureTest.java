package com.raphaelcollin.inventorymanagement.architecture;

import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class LayeredArchitectureTest extends ArchitectureBaseTest {

    private static final String API_LAYER = "Api layer";
    private static final String DOMAIN_LAYER = "Domain layer";
    private static final String INFRASTRUCTURE_LAYER = "Infrastructure layer";

    private static final Architectures.LayeredArchitecture ONION_ARCHITECTURE =
            Architectures
                    .layeredArchitecture()
                    .layer(DOMAIN_LAYER).definedBy(DOMAIN_LAYER_PACKAGES)
                    .layer(API_LAYER).definedBy(API_LAYER_PACKAGES)
                    .layer(INFRASTRUCTURE_LAYER).definedBy(INFRASTRUCTURE_LAYER_PACKAGES);

    @Nested
    @DisplayName("architecture: Api Layer")
    class ApiLayer {

        @Test
        @DisplayName("Api layer should only be accessed by Infrastructure layer")
        void givenApi_shouldOnlyBeAccessedByInfrastructureLayer() {
            ONION_ARCHITECTURE
                    .whereLayer(API_LAYER)
                    .mayOnlyBeAccessedByLayers(INFRASTRUCTURE_LAYER)
                    .check(classes);
        }
    }

    @Nested
    @DisplayName("architecture: Domain Layer")
    class DomainLayer {

        @Test
        @DisplayName("Domain layer should be accessed by Api and Infrastructure layers")
        void givenDomain_shouldBeAccessedByOtherLayers() {
            ONION_ARCHITECTURE
                    .whereLayer(DOMAIN_LAYER)
                    .mayOnlyBeAccessedByLayers(API_LAYER, INFRASTRUCTURE_LAYER)
                    .check(classes);
        }

        @Test
        @DisplayName("Domain layer should not access Api layer")
        void givenDomain_shouldNotAccessOtherLayers() {
            noClasses()
                    .that().resideInAPackage(DOMAIN_LAYER_PACKAGES)
                    .should().dependOnClassesThat().resideInAnyPackage(API_LAYER_PACKAGES)
                    .check(classes);
        }
    }

    @Nested
    @DisplayName("architecture: Infrastructure Layer")
    class InfrastructureLayer {

        @Test
        @DisplayName("Infrastructure layer should not be accessed by any layer")
        void givenApi_shouldOnlyBeAccessedByInfrastructureLayer() {
            ONION_ARCHITECTURE
                    .whereLayer(INFRASTRUCTURE_LAYER)
                    .mayNotBeAccessedByAnyLayer()
                    .check(classes);
        }
    }
}
