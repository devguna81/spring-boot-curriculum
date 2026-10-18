package com.jqdom.curriculum.ch36.member.internal;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jqdom.curriculum.ch36.common.Chunks;
import com.jqdom.curriculum.ch36.member.api.MemberApi;
import com.jqdom.curriculum.ch36.member.api.MemberGrade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * member 모듈의 구현. 값 목록은 모듈 안에서 묶음으로 나누어 IN으로 조회하고,
 * 등급은 모듈 안의 규칙 묶음(Composite)이 정한다. 호출하는 쪽은 둘 다 모른다.
 */
@Service
class MemberDirectory implements MemberApi {

    private final MemberMapper mapper;
    private final int inChunkSize;
    private final GradeRule gradeRule = new FirstMatchGradeRule(
            List.of(new DormantRule(), new SeniorRule(2020)), "REGULAR");

    MemberDirectory(MemberMapper mapper, @Value("${bulk.in-chunk-size}") int inChunkSize) {
        this.mapper = mapper;
        this.inChunkSize = inChunkSize;
    }

    @Override
    public Map<Long, MemberGrade> gradesOf(Collection<Long> memberIds) {
        Map<Long, MemberGrade> result = new LinkedHashMap<>();
        for (MemberRow row : Chunks.lookup(memberIds, inChunkSize, mapper::findByIds)) {
            String grade = gradeRule.grade(row).orElseThrow();
            result.put(row.id(), new MemberGrade(row.id(), row.name(), grade));
        }
        return result;
    }

    @Override
    public List<Long> memberIdsAfter(long lastId, int limit) {
        return mapper.idsAfter(lastId, limit);
    }
}
