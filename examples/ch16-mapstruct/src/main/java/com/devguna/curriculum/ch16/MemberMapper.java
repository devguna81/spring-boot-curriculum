package com.devguna.curriculum.ch16;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 컴파일할 때 이 클래스의 구현(MemberMapperImpl)이 생성된다. 리플렉션을 쓰지 않는 평범한 getter/setter 호출 코드다.
 * componentModel = "spring"이면 구현 클래스가 Spring Bean으로 등록되어 주입받을 수 있다.
 */
@Mapper(componentModel = "spring")
public abstract class MemberMapper {

    @Autowired
    protected PasswordHasher passwordHasher;

    /** 요청 -> 도메인. 서버가 정하는 값(id, status, createdAt)은 여기서 채우지 않는다. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password", qualifiedByName = "hash")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    public abstract Member toDomain(CreateMemberRequest request);

    /** 도메인 -> 응답. 이름이 다르거나 가공이 필요한 필드는 source와 qualifiedByName으로 연결한다. */
    @Mapping(target = "maskedEmail", source = "email", qualifiedByName = "maskEmail")
    @Mapping(target = "statusLabel", source = "status")
    @Mapping(target = "joinedDate", source = "createdAt", qualifiedByName = "date")
    public abstract MemberResponse toResponse(Member member);

    public abstract List<MemberResponse> toResponses(List<Member> members);

    /** 기존 도메인 객체를 제자리에서 고친다. null인 값은 건드리지 않는다. */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract void applyPatch(PatchMemberRequest request, @MappingTarget Member member);

    protected String statusLabel(MemberStatus status) {
        return status.label();
    }

    @Named("hash")
    protected String hash(String raw) {
        return passwordHasher.hash(raw);
    }

    @Named("maskEmail")
    protected String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return "*" + email.substring(at);
        }
        return email.charAt(0) + "*".repeat(at - 1) + email.substring(at);
    }

    @Named("date")
    protected String date(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
