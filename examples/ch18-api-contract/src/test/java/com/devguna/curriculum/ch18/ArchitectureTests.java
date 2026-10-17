package com.devguna.curriculum.ch18;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleName;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;

import org.junit.jupiter.api.Test;

/** 패키지 사이의 의존 방향을 테스트로 고정한다. 규칙을 어기는 코드가 들어오면 빌드가 실패한다. */
class ArchitectureTests {

    private static final String BASE = "com.devguna.curriculum.ch18";

    private final JavaClasses production = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    /** 계약 패키지는 구현 쪽(internal, web)을 몰라야 한다. */
    private static ArchRule contractIsIndependent(String contractPackage, String... forbiddenPackages) {
        return noClasses().that().resideInAPackage(contractPackage)
                .should().dependOnClassesThat().resideInAnyPackage(forbiddenPackages);
    }

    /**
     * web이 internal에서 쓸 수 있는 것은 서비스 입구와 예외뿐이다. 저장용 객체와 저장소는 쓰지 못한다.
     * 처음에는 클래스 이름이 Entity로 끝나는지로 막으려 했으나 ResponseEntity까지 걸려서, 허용 목록 방식으로 바꿨다.
     */
    private static ArchRule webDoesNotTouchPersistence(String webPackage) {
        return noClasses().that().resideInAPackage(webPackage)
                .should().dependOnClassesThat(resideInAPackage("..internal..")
                        .and(not(simpleName("UserApplicationService")))
                        .and(not(simpleNameEndingWith("Exception"))));
    }

    @Test
    void contractDoesNotDependOnInternalOrWeb() {
        contractIsIndependent("..ch18.contract..", "..ch18.internal..", "..ch18.web..").check(production);
    }

    @Test
    void webUsesOnlyTheServiceEntrancePointOfInternal() {
        webDoesNotTouchPersistence("..ch18.web..").check(production);
    }

    @Test
    void productionCodeHasClassesInEveryLayer() {
        // 규칙이 아무것도 검사하지 않는 상태(패키지 이름 오타 등)를 막는다.
        assertTrue(production.stream().anyMatch(c -> c.getPackageName().endsWith(".contract")));
        assertTrue(production.stream().anyMatch(c -> c.getPackageName().endsWith(".internal")));
        assertTrue(production.stream().anyMatch(c -> c.getPackageName().endsWith(".web")));
    }

    @Test
    void ruleActuallyDetectsAContractThatDependsOnInternal() {
        JavaClasses fixtures = new ClassFileImporter().importPackages(BASE + ".fixture");

        EvaluationResult result = contractIsIndependent("..fixture.contract..", "..fixture.internal..")
                .evaluate(fixtures);

        assertTrue(result.hasViolation());
    }

    @Test
    void ruleActuallyDetectsAClassThatReachesIntoInternal() {
        JavaClasses fixtures = new ClassFileImporter().importPackages(BASE + ".fixture");

        assertTrue(webDoesNotTouchPersistence("..fixture.contract..").evaluate(fixtures).hasViolation());
    }

    @Test
    void ruleIsQuietForCompliantClasses() {
        JavaClasses fixtures = new ClassFileImporter().importPackages(BASE + ".fixture.internal");

        assertFalse(contractIsIndependent("..fixture.internal..", "..fixture.contract..").evaluate(fixtures).hasViolation());
    }
}
