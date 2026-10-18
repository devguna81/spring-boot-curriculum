package com.jqdom.curriculum.ch36;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

import org.junit.jupiter.api.Test;

/** 호출 방향을 테스트로 고정한다. 모듈은 서로를 모르고, 오케스트레이션만 모듈 api를 부른다. */
class ArchitectureTests {

    private static final String BASE = "com.jqdom.curriculum.ch36";

    private final JavaClasses production = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    @Test
    void noCyclesBetweenTopLevelPackages() {
        slices().matching(BASE + ".(*)..").should().beFreeOfCycles().check(production);
    }

    @Test
    void onlyApiPackagesAreUsedFromOutsideTheModule() {
        for (String module : new String[] {"member", "reward", "order"}) {
            noClasses().that().resideOutsideOfPackage("..ch36." + module + "..")
                    .should().dependOnClassesThat().resideInAPackage("..ch36." + module + ".internal..")
                    .check(production);
        }
    }

    @Test
    void modulesDoNotKnowEachOtherOrTheCaller() {
        for (String module : new String[] {"member", "reward", "order"}) {
            noClasses().that().resideInAPackage("..ch36." + module + "..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..ch36.orchestration..", "..ch36.web..", "..ch36.batch..",
                            "..ch36." + otherThan(module, 0) + "..", "..ch36." + otherThan(module, 1) + "..")
                    .check(production);
        }
    }

    @Test
    void controllerDoesNotCallModulesDirectly() {
        noClasses().that().resideInAPackage("..ch36.web..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..ch36.member..", "..ch36.reward..", "..ch36.order..")
                .check(production);
    }

    private static String otherThan(String module, int index) {
        String[] all = {"member", "reward", "order"};
        int seen = 0;
        for (String candidate : all) {
            if (!candidate.equals(module)) {
                if (seen == index) {
                    return candidate;
                }
                seen++;
            }
        }
        throw new IllegalStateException();
    }
}
