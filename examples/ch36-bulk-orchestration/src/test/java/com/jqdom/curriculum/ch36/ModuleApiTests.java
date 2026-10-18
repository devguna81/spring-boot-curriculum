package com.jqdom.curriculum.ch36;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jqdom.curriculum.ch36.member.api.MemberApi;
import com.jqdom.curriculum.ch36.member.api.MemberGrade;
import com.jqdom.curriculum.ch36.order.api.OrderApi;
import com.jqdom.curriculum.ch36.reward.api.RewardApi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** 모듈 api 계약(빈 목록, 중복, 없는 id)과 IN 묶음 경계를 실제 DB로 확인한다. 묶음 크기를 2로 줄여 경계를 만든다. */
@SpringBootTest(properties = "bulk.in-chunk-size=2")
class ModuleApiTests {

    @Autowired
    MemberApi members;

    @Autowired
    RewardApi rewards;

    @Autowired
    OrderApi orders;

    @Test
    void emptyListGivesEmptyResult() {
        assertTrue(members.gradesOf(List.of()).isEmpty());
        assertTrue(rewards.pointsOf(List.of()).isEmpty());
        assertTrue(orders.orderCountsOf(List.of()).isEmpty());
    }

    @Test
    void unknownIdsHaveNoKey() {
        Map<Long, MemberGrade> result = members.gradesOf(List.of(1L, 99L));
        assertEquals(Set.of(1L), result.keySet());
    }

    @Test
    void duplicatesAreQueriedOnce() {
        assertEquals(Set.of(1L, 2L), members.gradesOf(List.of(1L, 1L, 2L, 2L)).keySet());
    }

    @Test
    void allFiveMembersAcrossChunkBoundaries() {
        // 묶음 크기 2: [1,2] [3,4] [5]
        Map<Long, MemberGrade> result = members.gradesOf(List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(5, result.size());
        assertEquals("SENIOR", result.get(1L).grade());
        assertEquals("REGULAR", result.get(2L).grade());
        assertEquals("DORMANT", result.get(3L).grade());
        assertEquals("REGULAR", result.get(4L).grade());
        assertEquals("SENIOR", result.get(5L).grade());
    }

    @Test
    void pointsAreSummedPerMemberAcrossChunks() {
        Map<Long, Long> points = rewards.pointsOf(List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(Map.of(1L, 600L, 2L, 100L, 4L, 600L), points);
    }

    @Test
    void orderCountsAreGroupedPerMember() {
        Map<Long, Long> counts = orders.orderCountsOf(List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(Map.of(1L, 3L, 2L, 1L, 5L, 2L), counts);
    }

    @Test
    void idsAreReadInKeysetPages() {
        assertEquals(List.of(1L, 2L), members.memberIdsAfter(0, 2));
        assertEquals(List.of(3L, 4L), members.memberIdsAfter(2, 2));
        assertEquals(List.of(5L), members.memberIdsAfter(4, 2));
        assertTrue(members.memberIdsAfter(5, 2).isEmpty());
    }
}
