package com.jqdom.curriculum.ch24.application.usecase;

import com.jqdom.curriculum.ch24.application.port.AddressLookupPort;
import com.jqdom.curriculum.ch24.application.port.MemberStore;
import com.jqdom.curriculum.ch24.domain.Address;
import com.jqdom.curriculum.ch24.domain.Member;

import org.springframework.stereotype.Service;

/** 우편번호로 주소를 확인한 뒤 회원을 저장한다. 두 협력은 모두 Port로만 안다. */
@Service
public class RegisterMemberService implements RegisterMemberUseCase {

    private final AddressLookupPort addressLookup;
    private final MemberStore store;

    public RegisterMemberService(AddressLookupPort addressLookup, MemberStore store) {
        this.addressLookup = addressLookup;
        this.store = store;
    }

    @Override
    public Member register(String name, String postalCode) {
        Address address = addressLookup.lookup(postalCode)
                .orElseThrow(() -> new UnknownPostalCodeException(postalCode));
        return store.save(name, address);
    }

    @Override
    public Member get(long id) {
        return store.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
    }
}
