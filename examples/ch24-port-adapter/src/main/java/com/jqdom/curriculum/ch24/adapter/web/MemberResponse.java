package com.jqdom.curriculum.ch24.adapter.web;

import com.jqdom.curriculum.ch24.domain.Member;

public record MemberResponse(long id, String name, AddressView address) {

    public record AddressView(String postalCode, String city, String district, String street) {
    }

    static MemberResponse from(Member m) {
        var a = m.address();
        return new MemberResponse(m.id(), m.name(),
                new AddressView(a.postalCode(), a.city(), a.district(), a.street()));
    }
}
