package com.jqdom.curriculum.finalproject;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

import org.junit.jupiter.api.Test;

/** f01에서 정한 구조 규칙. 이후 단계에서 모듈이 추가되어도 이 규칙은 유지된다. */
class ArchitectureTests {

    private static final String BASE = "com.jqdom.curriculum.finalproject";

    private final JavaClasses production = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    @Test
    void contractDependsOnNothingInsideTheApplication() {
        noClasses().that().resideInAPackage("..finalproject.contract..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..finalproject.user..", "..finalproject.post..", "..finalproject.web..", "..finalproject.common..")
                .check(production);
    }

    @Test
    void contractHasClasses() {
        assertTrue(production.stream().anyMatch(c -> c.getPackageName().endsWith(".contract")));
    }

    @Test
    void modulesAreOnlyReachedThroughTheirApiPackage() {
        noClasses().that().resideOutsideOfPackage("..finalproject.user..")
                .should().dependOnClassesThat().resideInAPackage("..finalproject.user.internal..")
                .check(production);
        noClasses().that().resideOutsideOfPackage("..finalproject.post..")
                .should().dependOnClassesThat().resideInAPackage("..finalproject.post.internal..")
                .check(production);
    }

    @Test
    void userModuleDoesNotKnowPostModule() {
        noClasses().that().resideInAPackage("..finalproject.user..")
                .should().dependOnClassesThat().resideInAPackage("..finalproject.post..")
                .check(production);
    }

    @Test
    void commonDoesNotDependOnModulesOrWeb() {
        noClasses().that().resideInAPackage("..finalproject.common..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..finalproject.user..", "..finalproject.post..", "..finalproject.web..")
                .check(production);
    }

    @Test
    void postModuleDoesNotKnowSpringSecurity() {
        noClasses().that().resideInAPackage("..finalproject.post..")
                .should().dependOnClassesThat().resideInAPackage("org.springframework.security..")
                .check(production);
    }

    @Test
    void onlySecurityAndWebTouchSecurityContext() {
        noClasses().that().resideOutsideOfPackages("..finalproject.security..", "..finalproject.web..")
                .should().dependOnClassesThat().haveFullyQualifiedName(
                        "org.springframework.security.core.context.SecurityContextHolder")
                .check(production);
    }

    @Test
    void securityDoesNotDependOnPostOrUserInternals() {
        noClasses().that().resideInAPackage("..finalproject.security..")
                .should().dependOnClassesThat().resideInAnyPackage("..finalproject.post..", "..finalproject.user.internal..")
                .check(production);
    }
}
