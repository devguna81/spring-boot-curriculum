package com.jqdom.curriculum.ch36.orchestration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jqdom.curriculum.ch36.member.api.MemberGrade;

/**
 * Handler들이 각자 얻은 값을 타입을 지켜 채워 넣는 초안. 모든 Handler가 끝나면 요약 목록으로 만든다.
 * 값이 채워지지 않은 항목(호출 실패 등)은 null로 남는다.
 */
public class SummaryDraft {

    private final Map<Long, MemberGrade> members = new HashMap<>();
    private final Map<Long, Long> points = new HashMap<>();
    private final Map<Long, Long> orderCounts = new HashMap<>();
    private boolean pointsLoaded;
    private boolean orderCountsLoaded;

    public void putMembers(Map<Long, MemberGrade> values) {
        members.putAll(values);
    }

    public void putPoints(Map<Long, Long> values) {
        points.putAll(values);
        pointsLoaded = true;
    }

    public void putOrderCounts(Map<Long, Long> values) {
        orderCounts.putAll(values);
        orderCountsLoaded = true;
    }

    public List<MemberSummary> summaries(List<Long> requestedIds) {
        List<MemberSummary> result = new ArrayList<>();
        for (Long id : requestedIds) {
            MemberGrade member = members.get(id);
            if (member == null) {
                continue;
            }
            // 값을 가져오는 데 성공했는데 키가 없으면 "기록 없음"이라 0, 가져오지 못했으면 null.
            Long point = pointsLoaded ? points.getOrDefault(id, 0L) : null;
            Long count = orderCountsLoaded ? orderCounts.getOrDefault(id, 0L) : null;
            result.add(new MemberSummary(id, member.name(), member.grade(), point, count));
        }
        return result;
    }

    public List<Long> missingIds(List<Long> requestedIds) {
        return requestedIds.stream().filter(id -> !members.containsKey(id)).toList();
    }
}
