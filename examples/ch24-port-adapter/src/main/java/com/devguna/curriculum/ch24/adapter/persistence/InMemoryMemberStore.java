package com.devguna.curriculum.ch24.adapter.persistence;

import com.devguna.curriculum.ch24.application.port.MemberStore;
import com.devguna.curriculum.ch24.domain.Address;
import com.devguna.curriculum.ch24.domain.Member;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryMemberStore implements MemberStore {

    private final Map<Long, Member> members = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public Member save(String name, Address address) {
        Member member = new Member(sequence.incrementAndGet(), name, address);
        members.put(member.id(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(long id) {
        return Optional.ofNullable(members.get(id));
    }
}
