package com.jqdom.curriculum.ch16;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberMapperTests {

    @Autowired
    MemberMapper mapper;

    @Autowired
    PasswordHasher hasher;

    private Member member() {
        Member member = new Member();
        member.setId(7L);
        member.setName("kim");
        member.setEmail("kimberly@example.com");
        member.setAge(28);
        member.setPasswordHash("secret-hash");
        member.setStatus(MemberStatus.ACTIVE);
        member.setCreatedAt(LocalDateTime.of(2026, 10, 17, 9, 30));
        return member;
    }

    @Test
    void generatedImplementationIsAPlainClassNotReflection() {
        assertEquals("MemberMapperImpl", mapper.getClass().getSimpleName());
    }

    @Test
    void requestBecomesDomainWithHashedPasswordAndServerFields() {
        Member member = mapper.toDomain(new CreateMemberRequest("kim", "kim@example.com", 28, "password-1"));

        assertEquals("kim", member.getName());
        assertEquals(28, member.getAge());
        assertEquals(hasher.hash("password-1"), member.getPasswordHash());
        assertNotEquals("password-1", member.getPasswordHash());
        assertEquals(MemberStatus.ACTIVE, member.getStatus());
        assertNull(member.getId());
        assertNull(member.getCreatedAt());
    }

    @Test
    void domainBecomesResponseWithoutSecretsAndWithDerivedValues() {
        MemberResponse response = mapper.toResponse(member());

        assertEquals(new MemberResponse(7, "kim", "k*******@example.com", 28, "활동", "2026-10-17"), response);
    }

    @Test
    void responseTypeHasNoFieldForThePasswordHash() {
        List<String> names = java.util.Arrays.stream(MemberResponse.class.getRecordComponents())
                .map(java.lang.reflect.RecordComponent::getName).toList();

        assertEquals(List.of("id", "name", "maskedEmail", "age", "statusLabel", "joinedDate"), names);
    }

    @Test
    void listMappingConvertsEveryElement() {
        List<MemberResponse> responses = mapper.toResponses(List.of(member(), member()));

        assertEquals(2, responses.size());
    }

    @Test
    void patchOverwritesOnlyProvidedFields() {
        Member member = member();

        mapper.applyPatch(new PatchMemberRequest(null, null, 40), member);

        assertEquals(40, member.getAge());
        assertEquals("kim", member.getName());
        assertEquals("kimberly@example.com", member.getEmail());
        assertEquals("secret-hash", member.getPasswordHash());
        assertEquals(MemberStatus.ACTIVE, member.getStatus());
    }

    @Test
    void shortLocalPartIsMaskedWithoutExposingIt() {
        Member member = member();
        member.setEmail("a@example.com");

        assertEquals("*@example.com", mapper.toResponse(member).maskedEmail());
    }
}
