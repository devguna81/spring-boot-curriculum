package com.jqdom.curriculum.ch36.reward.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jqdom.curriculum.ch36.common.Chunks;
import com.jqdom.curriculum.ch36.reward.api.RewardApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class RewardService implements RewardApi {

    private final RewardMapper mapper;
    private final int inChunkSize;
    private final PointRule pointRule = new SumPointRule(List.of(new EarnRule(), new UseRule()));

    RewardService(RewardMapper mapper, @Value("${bulk.in-chunk-size}") int inChunkSize) {
        this.mapper = mapper;
        this.inChunkSize = inChunkSize;
    }

    @Override
    public Map<Long, Long> pointsOf(Collection<Long> memberIds) {
        Map<Long, List<LedgerRow>> byMember = new LinkedHashMap<>();
        for (LedgerRow row : Chunks.lookup(memberIds, inChunkSize, mapper::ledgerByMemberIds)) {
            byMember.computeIfAbsent(row.memberId(), key -> new ArrayList<>()).add(row);
        }
        Map<Long, Long> result = new LinkedHashMap<>();
        byMember.forEach((memberId, rows) -> result.put(memberId, pointRule.points(rows)));
        return result;
    }
}
