package com.jqdom.curriculum.ch36;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.jqdom.curriculum.ch36.member.internal.MemberDirectoryTestAccess;

import org.junit.jupiter.api.Test;

/** 모듈 안 Composite(등급 규칙, 포인트 규칙)를 같은 패키지의 접근 도우미로 확인한다. */
class ModuleRuleTests {

    @Test
    void gradeFirstMatchWins() {
        assertEquals("DORMANT", MemberDirectoryTestAccess.grade(2010, true));
        assertEquals("SENIOR", MemberDirectoryTestAccess.grade(2020, false));
        assertEquals("REGULAR", MemberDirectoryTestAccess.grade(2021, false));
    }

    @Test
    void pointsAreEarnPlusUse() {
        assertEquals(600, com.jqdom.curriculum.ch36.reward.internal.RewardRuleTestAccess.points(List.of(500L, 300L, -200L)));
        assertEquals(0, com.jqdom.curriculum.ch36.reward.internal.RewardRuleTestAccess.points(List.of()));
    }
}
