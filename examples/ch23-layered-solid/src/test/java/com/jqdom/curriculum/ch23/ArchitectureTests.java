package com.jqdom.curriculum.ch23;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.INTERFACES;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.base.DescribedPredicate.not;

/** 계층 책임과 의존 방향(DIP)을 테스트로 고정한다. */
class ArchitectureTests {

    private static final String BASE = "com.jqdom.curriculum.ch23";

    private final JavaClasses production = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    /** 테스트 소스까지 읽어서 위반 예제(fixture)를 규칙에 걸어 보는 데 쓴다. */
    private final JavaClasses withFixtures = new ClassFileImporter().importPackages(BASE);

    private static ArchRule layers() {
        return layeredArchitecture().consideringOnlyDependenciesInLayers()
                .layer("Web").definedBy("..ch23.web..")
                .layer("Service").definedBy("..ch23.service..")
                .layer("Repository").definedBy("..ch23.repository..")
                .layer("Notification").definedBy("..ch23.notification..")
                .whereLayer("Web").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Web")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Notification").mayOnlyBeAccessedByLayers("Service");
    }

    /** 컨트롤러는 저장소를 직접 쓰지 못한다. 서비스를 거쳐야 한다. */
    private static ArchRule webDoesNotUseRepository(String webPackage) {
        return noClasses().that().resideInAPackage(webPackage)
                .should().dependOnClassesThat().resideInAPackage("..repository..");
    }

    @Test
    void layersOnlyDependDownward() {
        layers().check(production);
    }

    @Test
    void controllerDoesNotTouchRepository() {
        webDoesNotUseRepository("..ch23.web..").check(production);
    }

    @Test
    void serviceDependsOnlyOnInterfacesOfItsCollaborators() {
        noClasses().that().resideInAPackage("..ch23.service..")
                .should().dependOnClassesThat(
                        resideInAnyPackage("..ch23.repository..", "..ch23.notification..").and(not(INTERFACES)))
                .check(production);
    }

    @Test
    void domainDoesNotDependOnAnyLayer() {
        noClasses().that().resideInAPackage("..ch23.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..ch23.web..", "..ch23.service..", "..ch23.repository..", "..ch23.notification..")
                .check(production);
    }

    @Test
    void fatControllerFixtureIsCaughtByTheRule() {
        // 규칙이 실제로 위반을 잡아내는지 확인한다. 항상 통과하는 규칙은 아무것도 보호하지 못한다.
        var result = webDoesNotUseRepository("..fixture.violation..").evaluate(withFixtures);
        assertTrue(result.hasViolation(), "위반 예제를 규칙이 잡지 못했다");
    }

    @Test
    void everyLayerHasClasses() {
        for (String layer : new String[] {"web", "service", "repository", "notification", "domain"}) {
            assertTrue(production.stream().anyMatch(c -> c.getPackageName().endsWith("." + layer)),
                    layer + " 패키지에 클래스가 없다. 규칙이 아무것도 검사하지 않는 상태다");
        }
    }
}
