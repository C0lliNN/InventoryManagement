package com.raphaelcollin.inventorymanagement.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;


public class ComponentsArchitectureTest extends ArchitectureBaseTest {

    @Nested
    @DisplayName("architecture: Controllers Test")
    class ControllersTest {

        @Test
        @DisplayName("Controllers should be accordingly annotated and reside in Infrastructure package")
        void givenController_shouldAnnotated() {
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .and().haveSimpleNameNotContaining("Exception")
                    .should().beAnnotatedWith(RestController.class)
                    .andShould().resideInAPackage(INFRASTRUCTURE_LAYER_PACKAGES)
                    .check(classes);
        }

        @Test
        @DisplayName("Controller Advices should be accordingly annotated and reside in Infrastructure package")
        void givenControllerAdvice_shouldAnnotated() {
            classes()
                    .that().haveSimpleName("ExceptionController")
                    .should().beAnnotatedWith(ControllerAdvice.class)
                    .andShould().resideInAPackage(INFRASTRUCTURE_LAYER_PACKAGES)
                    .check(classes);
        }

        @Test
        @DisplayName("Controllers should not access repository components")
        void givenController_shouldNotAccessRepositoryComponents() {
            noClasses().that().areAnnotatedWith(RestController.class)
                    .or().areAnnotatedWith(ControllerAdvice.class)
                    .should().dependOnClassesThat().resideInAnyPackage("..infrastructure.mongodb..")
                    .check(classes);
        }
    }

    @Nested
    @DisplayName("architecture: Router Test")
    class RouterTest {

        @Test
        @DisplayName("routers should be accordingly annotated and reside in the Infrastructure package")
        void givenRouter_shouldBeAnnotated() {
            classes()
                    .that().haveSimpleNameEndingWith("Router")
                    .should().beAnnotatedWith(Configuration.class)
                    .andShould().resideInAPackage(INFRASTRUCTURE_LAYER_PACKAGES)
                    .check(classes);
        }
    }

    @Nested
    @DisplayName("architecture: Handler Test")
    class HandlerTest {

        @Test
        @DisplayName("handlers should be accordingly annotated and reside in the Infrastructure package")
        void givenHandler_shouldBeAnnotated() {
            classes()
                    .that().haveSimpleNameEndingWith("Handler")
                    .should().beAnnotatedWith(Component.class)
                    .andShould().resideInAPackage(INFRASTRUCTURE_LAYER_PACKAGES)
                    .check(classes);
        }

        @Test
        @DisplayName("handlers should not access repositories components")
        void handlersShouldNotAccessRepositoriesComponents() {
            noClasses()
                    .that().haveSimpleNameEndingWith("Handler")
                    .should().dependOnClassesThat().resideInAnyPackage("..infrastructure.mongodb..")
                    .check(classes);
        }
    }

    @Nested
    @DisplayName("architecture: Service Test")
    class ServiceTest {

        @Test
        @DisplayName("Service should be accordingly annotated and reside in Domain package")
        void givenService_shouldAnnotated() {
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class)
                    .andShould().resideInAPackage(DOMAIN_LAYER_PACKAGES)
                    .check(classes);
        }

        @Test
        @DisplayName("Service should not access Dtos")
        void givenService_shouldNotAccessDtos() {
            noClasses()
                    .that().areAnnotatedWith(Service.class)
                    .should().dependOnClassesThat().resideInAPackage("..api.dto..")
                    .check(classes);
        }
    }

    @Nested
    @DisplayName("architecture: Repository Test")
    class RepositoryTest {

        @Test
        @DisplayName("Repository should be accordingly annotated and reside in ..infrastructure.sql package")
        void givenRepository_shouldAnnotated() {
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .and().resideInAPackage("..infrastructure.sql")
                    .should().beAnnotatedWith(Repository.class)
                    .check(classes);
        }
    }
}
