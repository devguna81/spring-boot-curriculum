package com.jqdom.curriculum.ch24;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jqdom.curriculum.ch24.adapter.persistence.InMemoryMemberStore;
import com.jqdom.curriculum.ch24.application.port.AddressLookupPort;
import com.jqdom.curriculum.ch24.application.port.AddressLookupUnavailableException;
import com.jqdom.curriculum.ch24.application.usecase.MemberNotFoundException;
import com.jqdom.curriculum.ch24.application.usecase.RegisterMemberService;
import com.jqdom.curriculum.ch24.application.usecase.UnknownPostalCodeException;
import com.jqdom.curriculum.ch24.domain.Address;
import com.jqdom.curriculum.ch24.domain.Member;

import org.junit.jupiter.api.Test;

import java.util.Optional;

/** 외부 API 없이 응용 계층만 검사한다. Port가 메서드 하나짜리라서 람다로 대신할 수 있다. */
class RegisterMemberServiceTests {

    private static final Address GANGNAM = new Address("06236", "서울", "강남구", "테헤란로");

    private final InMemoryMemberStore store = new InMemoryMemberStore();

    private RegisterMemberService serviceWith(AddressLookupPort port) {
        return new RegisterMemberService(port, store);
    }

    @Test
    void savesMemberWithLookedUpAddress() {
        RegisterMemberService service = serviceWith(code -> Optional.of(GANGNAM));

        Member saved = service.register("kim", "06236");

        assertEquals(GANGNAM, saved.address());
        assertEquals(saved, service.get(saved.id()));
    }

    @Test
    void unknownPostalCodeIsRejected() {
        RegisterMemberService service = serviceWith(code -> Optional.empty());

        assertThrows(UnknownPostalCodeException.class, () -> service.register("kim", "00000"));
    }

    @Test
    void unavailableLookupPropagatesAndSavesNothing() {
        RegisterMemberService service = serviceWith(code -> {
            throw new AddressLookupUnavailableException("down", null);
        });

        assertThrows(AddressLookupUnavailableException.class, () -> service.register("kim", "06236"));
        assertThrows(MemberNotFoundException.class, () -> service.get(1));
    }
}
