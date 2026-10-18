package com.jqdom.curriculum.ch24.application.port;

import com.jqdom.curriculum.ch24.domain.Address;

import java.util.Optional;

/**
 * 응용 계층이 외부 주소 조회에 요구하는 것을 우리 말로 적은 약속(Port).
 * 어떤 주소 API를 어떻게 부르는지는 이 인터페이스의 구현(Adapter)이 정한다.
 */
public interface AddressLookupPort {

    /** 없는 우편번호면 빈 값을 돌려준다. 외부가 응답하지 못하면 {@link AddressLookupUnavailableException}. */
    Optional<Address> lookup(String postalCode);
}
