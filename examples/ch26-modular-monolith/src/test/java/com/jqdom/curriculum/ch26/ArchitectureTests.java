package com.jqdom.curriculum.ch26;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.Test;

/** 모듈 경계를 테스트로 고정한다. 경계를 넘는 코드가 들어오면 빌드가 실패한다. */
class ArchitectureTests {

    private static final String BASE = "com.jqdom.curriculum.ch26";

    private final JavaClasses production = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    private final JavaClasses withFixtures = new ClassFileImporter().importPackages(BASE);

    private static ArchRule modulesAreFreeOfCycles(String slicePattern) {
        return slices().matching(slicePattern).should().beFreeOfCycles();
    }

    private static ArchRule internalIsNotPublic(String internalPackage) {
        return classes().that().resideInAPackage(internalPackage)
                .and().areTopLevelClasses()
                .should().notBePublic();
    }

    @Test
    void modulesHaveNoCycles() {
        modulesAreFreeOfCycles(BASE + ".(*)..").check(production);
    }

    @Test
    void cycleFixtureIsCaughtByTheRule() {
        var result = modulesAreFreeOfCycles(BASE + ".fixture.cycle.(*)..").evaluate(withFixtures);
        assertTrue(result.hasViolation(), "순환 예제를 규칙이 잡지 못했다");
    }

    @Test
    void internalClassesAreNotPublic() {
        internalIsNotPublic("..ch26..internal..").check(production);
    }

    @Test
    void publicInternalFixtureIsCaughtByTheRule() {
        var result = internalIsNotPublic("..fixture.exposed.internal..").evaluate(withFixtures);
        assertTrue(result.hasViolation(), "public internal 예제를 규칙이 잡지 못했다");
    }

    @Test
    void otherModulesUseOnlyTheApiPackage() {
        for (String module : new String[] {"member", "catalog", "order", "reward"}) {
            noClasses().that().resideOutsideOfPackage("..ch26." + module + "..")
                    .should().dependOnClassesThat().resideInAPackage("..ch26." + module + ".internal..")
                    .check(production);
        }
    }

    @Test
    void allowedModuleDependenciesOnly() {
        // member와 catalog는 다른 모듈을 하나도 모른다.
        noClasses().that().resideInAPackage("..ch26.member..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..ch26.catalog..", "..ch26.order..", "..ch26.reward..")
                .check(production);
        noClasses().that().resideInAPackage("..ch26.catalog..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..ch26.member..", "..ch26.order..", "..ch26.reward..")
                .check(production);
        // order는 member.api, catalog.api만 쓰고 reward는 모른다.
        noClasses().that().resideInAPackage("..ch26.order..")
                .should().dependOnClassesThat().resideInAPackage("..ch26.reward..")
                .check(production);
        // reward는 order.api만 안다.
        noClasses().that().resideInAPackage("..ch26.reward..")
                .should().dependOnClassesThat().resideInAnyPackage("..ch26.member..", "..ch26.catalog..")
                .check(production);
    }

    @Test
    void apiPackagesDoNotDependOnTheirOwnInternal() {
        noClasses().that().resideInAPackage("..ch26..api..")
                .should().dependOnClassesThat().resideInAPackage("..ch26..internal..")
                .check(production);
    }

    @Test
    void everyModuleHasApiAndInternal() {
        for (String module : new String[] {"member", "catalog", "order", "reward"}) {
            for (String part : new String[] {"api", "internal"}) {
                String pkg = BASE + "." + module + "." + part;
                assertTrue(production.stream().anyMatch(c -> c.getPackageName().equals(pkg)), pkg + " 패키지에 클래스가 없다");
            }
        }
    }
}
