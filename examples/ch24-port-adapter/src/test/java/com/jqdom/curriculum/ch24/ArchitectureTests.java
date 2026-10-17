package com.jqdom.curriculum.ch24;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.Test;

/** Port/Adapter의 의존 방향: 바깥(adapter)이 안쪽(application, domain)을 향하고, 반대는 없다. */
class ArchitectureTests {

    private static final String BASE = "com.jqdom.curriculum.ch24";

    private final JavaClasses production = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    private final JavaClasses withFixtures = new ClassFileImporter().importPackages(BASE);

    private static ArchRule applicationDoesNotKnowAdapters(String applicationPackage) {
        return noClasses().that().resideInAPackage(applicationPackage)
                .should().dependOnClassesThat().resideInAPackage("..ch24.adapter..");
    }

    @Test
    void domainDependsOnNothingElse() {
        noClasses().that().resideInAPackage("..ch24.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("..ch24.application..", "..ch24.adapter..",
                        "org.springframework..")
                .check(production);
    }

    @Test
    void applicationDoesNotKnowAdapters() {
        applicationDoesNotKnowAdapters("..ch24.application..").check(production);
    }

    @Test
    void applicationDoesNotUseHttpClientOrWebTypes() {
        noClasses().that().resideInAPackage("..ch24.application..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework.web..",
                        "org.springframework.http..", "java.net.http..")
                .check(production);
    }

    @Test
    void adaptersDoNotDependOnEachOther() {
        noClasses().that().resideInAPackage("..ch24.adapter.web..")
                .should().dependOnClassesThat().resideInAnyPackage("..ch24.adapter.address..", "..ch24.adapter.persistence..")
                .check(production);
        noClasses().that().resideInAPackage("..ch24.adapter.address..")
                .should().dependOnClassesThat().resideInAnyPackage("..ch24.adapter.web..", "..ch24.adapter.persistence..")
                .check(production);
    }

    @Test
    void externalResponseTypeStaysInsideItsAdapter() {
        noClasses().that().resideOutsideOfPackage("..ch24.adapter.address..")
                .should().dependOnClassesThat().haveSimpleName("ExternalAddressResponse")
                .check(production);
    }

    @Test
    void leakyFixtureIsCaughtByTheRule() {
        var result = applicationDoesNotKnowAdapters("..fixture.leaky..").evaluate(withFixtures);
        assertTrue(result.hasViolation(), "위반 예제를 규칙이 잡지 못했다");
    }

    @Test
    void everyPartHasClasses() {
        for (String part : new String[] {"domain", "application.port", "application.usecase",
                "adapter.web", "adapter.address", "adapter.persistence"}) {
            assertTrue(production.stream().anyMatch(c -> c.getPackageName().endsWith("." + part)),
                    part + " 패키지에 클래스가 없다");
        }
    }
}
