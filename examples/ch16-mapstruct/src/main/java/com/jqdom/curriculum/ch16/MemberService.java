package com.jqdom.curriculum.ch16;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberMapper mapper;
    private final Clock clock;
    private final Map<Long, Member> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public MemberService(MemberMapper mapper, Clock clock) {
        this.mapper = mapper;
        this.clock = clock;
    }

    public Member create(CreateMemberRequest request) {
        Member member = mapper.toDomain(request);
        member.setId(sequence.incrementAndGet());
        member.setCreatedAt(LocalDateTime.now(clock));
        store.put(member.getId(), member);
        return member;
    }

    public List<Member> findAll() {
        return store.values().stream().sorted((a, b) -> Long.compare(a.getId(), b.getId())).toList();
    }

    public Optional<Member> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Member> patch(long id, PatchMemberRequest request) {
        return findById(id).map(member -> {
            mapper.applyPatch(request, member);
            return member;
        });
    }
}
